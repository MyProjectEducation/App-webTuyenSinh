import React, { useState } from 'react';
import { useAppContext, AdmissionResult, Preference } from '../context/AppContext';
import { admissionService } from '../services/admissionService';
import {
  PlayIcon,
  CheckCircleIcon,
  XCircleIcon,
  PlusIcon,
  TrashIcon,
  EditIcon,
  UploadIcon,
  Loader2
} from
  'lucide-react';
import { convertVsatToThpt } from '../utils/vsatConversion';
import { ImportModal } from '../components/ImportModal';
import Pagination from '../components/Pagination';

export function AdmissionProcess() {
  const {
    candidates,
    majors,
    admissionResults,
    setAdmissionResults,
    preferences,
    setPreferences,
    candidateScores,
    bonusPoints,
    subjectCombinations,
    majorCombinations,
    isLoading
  } = useAppContext();
  const [diemSan, setDiemSan] = useState('18');
  const [hasRun, setHasRun] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [currentPageResults, setCurrentPageResults] = useState(1);
  const itemsPerPage = 10;

  // Preference CRUD state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPref, setEditingPref] = useState<Preference | null>(null);
  const [formData, setFormData] = useState({
    cccd: '',
    hoTen: '',
    thuTuNV: '1',
    maNganh: '',
    maToHop: ''
  });
  // Bảng chênh lệch (Hàng: Tổ hợp gốc, Cột: Tổ hợp hiện tại)
  const DEVIATION_TABLE: Record<string, Record<string, number>> = {
    'A00': { 'A00': 0, 'A01': -0.69, 'B00': -1.21, 'C00': 2.32, 'C01': 0.94, 'D01': -0.68, 'D07': -1.62 },
    'A01': { 'A00': 0.69, 'A01': 0, 'B00': -0.52, 'C00': 3.01, 'C01': 1.63, 'D01': 0.01, 'D07': -0.93 },
    'B00': { 'A00': 1.21, 'A01': 0.52, 'B00': 0, 'C00': 3.53, 'C01': 2.15, 'D01': 0.53, 'D07': -0.41 },
    'C00': { 'A00': -2.32, 'A01': -3.01, 'B00': -3.53, 'C00': 0, 'C01': -1.38, 'D01': -3.00, 'D07': -3.94 },
    'C01': { 'A00': -0.94, 'A01': -1.63, 'B00': -2.15, 'C00': 1.38, 'C01': 0, 'D01': -1.62, 'D07': -2.56 },
    'D01': { 'A00': 0.68, 'A01': -0.01, 'B00': -0.53, 'C00': 3.00, 'C01': 1.62, 'D01': 0, 'D07': -0.94 }
  };

  const calculateTotalScore = (cccd: string, maNganh: string, maToHop: string) => {
    const combo = subjectCombinations.find((c) => c.maToHop === maToHop);
    const major = majors.find((m) => m.maNganh === maNganh);
    const majorCombo = majorCombinations.find((mc) => mc.maNganh === maNganh && mc.maToHop === maToHop);

    if (!combo || !major) return 0;

    // Lấy điểm
    const scores = candidateScores.filter((s) => s.cccd === cccd);

    const getConvertedScore = (monKey?: string) => {
      if (!monKey) return 0;
      const scoreObj = scores.find((s) => s.colName === monKey);
      if (!scoreObj) return 0;

      if (scoreObj.loaiDiem === 'VSAT') {
        return convertVsatToThpt(monKey, scoreObj.diem);
      }
      return scoreObj.diem;
    };

    const s1 = getConvertedScore(combo.mon1);
    const s2 = getConvertedScore(combo.mon2);
    const s3 = getConvertedScore(combo.mon3);

    // Lấy trọng số
    const w1 = majorCombo?.hsMon1 || 1;
    const w2 = majorCombo?.hsMon2 || 1;
    const w3 = majorCombo?.hsMon3 || 1;
    const W = w1 + w2 + w3;

    // ĐTHXT
    const dthxt = ((s1 * w1 + s2 * w2 + s3 * w3) / W) * 3;

    // ĐTHGXT
    const toHopGoc = major.toHopGoc || 'A00';
    let deviation = 0;
    if (DEVIATION_TABLE[toHopGoc] && DEVIATION_TABLE[toHopGoc][maToHop] !== undefined) {
      deviation = DEVIATION_TABLE[toHopGoc][maToHop];
    }
    const dthgxt = dthxt - deviation;

    return dthgxt;
  };

  const runAdmission = () => {
    const threshold = parseFloat(diemSan);
    const results: AdmissionResult[] = [];
    const majorAdmissions: {
      [key: string]: {
        admitted: number;
        quota: number;
      };
    } = {};
    majors.forEach((major) => {
      majorAdmissions[major.maNganh] = {
        admitted: 0,
        quota: major.chiTieu
      };
    });
    // Group preferences by candidate
    const candidatesWithPrefs = candidates.map((candidate) => {
      const cccd = candidate.cccd;
      const candidatePrefs = preferences.
        filter((p) => p.cccd === cccd).
        sort((a, b) => a.thuTuNV - b.thuTuNV); // Sort by preference order

      let bestScore = 0;
      let actualBonus = 0;
      let actualTotal = 0;

      candidatePrefs.forEach((pref) => {
        const bonusObj = bonusPoints.find((p) => p.cccd === cccd && p.maNganh === pref.maNganh && p.maToHop === pref.maToHop);

        // Điểm cộng tổng (đã áp trần 3.0 trong cơ sở dữ liệu)
        const dC_30 = bonusObj?.diemC || 0;
        const mDuT_30 = bonusObj?.diemUt || 0;
        const totalBonus = bonusObj?.diem || (dC_30 + mDuT_30);

        const dthgxt_raw = calculateTotalScore(cccd, pref.maNganh, pref.maToHop); // This returns Thang 30
        const dxt = dthgxt_raw + totalBonus;

        if (dxt > actualTotal) {
          bestScore = dthgxt_raw;
          actualBonus = totalBonus;
          actualTotal = dxt;
        }
      });
      return {
        candidate,
        prefs: candidatePrefs,
        baseScore: bestScore,
        bonusScore: actualBonus,
        totalScore: actualTotal
      };
    });
    // Sort all candidates by total score descending
    const sortedCandidates = candidatesWithPrefs.sort(
      (a, b) => b.totalScore - a.totalScore
    );
    // Admission logic
    sortedCandidates.forEach((item) => {
      let admitted = false;
      let admittedMajor = '';
      if (item.totalScore < threshold) {
        results.push({
          candidateId: item.candidate.id,
          cccd: item.candidate.cccd,
          hoTen: item.candidate.hoTen,
          diem: item.baseScore,
          diemCong: item.bonusScore,
          tongDiem: item.totalScore,
          nganhTrungTuyen: 'Không đủ điểm sàn',
          trangThai: 'Không đậu'
        });
        return;
      }
      // Try to admit to highest preference possible
      for (const pref of item.prefs) {
        const major = majorAdmissions[pref.maNganh];
        const majorInfo = majors.find(m => m.maNganh === pref.maNganh);
        const requiredScore = majorInfo?.diemTrungTuyen || threshold;

        if (major && major.admitted < major.quota && item.totalScore >= requiredScore) {
          major.admitted++;
          admitted = true;
          admittedMajor = pref.maNganh;
          break; // Stop checking preferences once admitted
        }
      }
      results.push({
        candidateId: item.candidate.id,
        cccd: item.candidate.cccd,
        hoTen: item.candidate.hoTen,
        diem: item.baseScore,
        diemCong: item.bonusScore,
        tongDiem: item.totalScore,
        nganhTrungTuyen: admitted ? admittedMajor : 'Hết chỉ tiêu',
        trangThai: admitted ? 'Đậu' : 'Không đậu'
      });
    });
    setAdmissionResults(results);
    setHasRun(true);
  };
  const handleAddPref = () => {
    setEditingPref(null);
    setFormData({
      cccd: '',
      hoTen: '',
      thuTuNV: '1',
      maNganh: '',
      maToHop: ''
    });
    setIsModalOpen(true);
  };
  const handleEditPref = (pref: Preference) => {
    setEditingPref(pref);
    setFormData({
      cccd: pref.cccd,
      hoTen: pref.hoTen,
      thuTuNV: pref.thuTuNV.toString(),
      maNganh: pref.maNganh,
      maToHop: pref.maToHop
    });
    setIsModalOpen(true);
  };
  const handleDeletePref = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa nguyện vọng này?')) {
      try {
        await admissionService.delete(id);
        setPreferences(preferences.filter((p) => p.id !== id));
      } catch (err) {
        alert("Lỗi thao tác");
      }
    }
  };
  const handleSubmitPref = async (e: React.FormEvent) => {
    e.preventDefault();

    // Kiểm tra trùng lặp nguyện vọng
    if (!editingPref) {
      const isDuplicateOrder = preferences.some(p => p.cccd === formData.cccd && p.thuTuNV.toString() === formData.thuTuNV);
      if (isDuplicateOrder) {
        alert(`Thí sinh này đã có Nguyện vọng ${formData.thuTuNV}! Vui lòng chọn thứ tự khác.`);
        return;
      }

      const isDuplicateMajor = preferences.some(p => p.cccd === formData.cccd && p.maNganh === formData.maNganh);
      if (isDuplicateMajor) {
        alert('Thí sinh này đã đăng ký ngành này rồi!');
        return;
      }
    }

    try {
      if (editingPref) {
        await admissionService.update(editingPref.id, {
          ...formData, thuTuNV: parseInt(formData.thuTuNV)
        });
        setPreferences(
          preferences.map((p) =>
            p.id === editingPref.id ?
              {
                ...p,
                ...formData,
                thuTuNV: parseInt(formData.thuTuNV)
              } :
              p
          )
        );
      } else {
        const result = await admissionService.create({
          ...formData, thuTuNV: parseInt(formData.thuTuNV)
        });
        const newPref: Preference = {
          id: result.id,
          ...formData,
          thuTuNV: parseInt(formData.thuTuNV)
        };
        setPreferences([...preferences, newPref]);
      }
      setIsModalOpen(false);
    } catch (err) {
      alert("Lỗi thao tác");
    }
  };

  const fetchPreferences = async () => {
    try {
      const data = await admissionService.getAll();
      setPreferences(data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleImport = async (file: File) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const result = await admissionService.importPreferences(formData);
      alert(result.message);

      fetchPreferences();
      setIsImportOpen(false);
    } catch (error: any) {
      console.error('Lỗi import:', error);
      alert(error.response?.data?.message || 'Lỗi khi import file Excel');
    }
  };

  const handleDownloadTemplate = () => {
    const headers = ['CCCD', 'Thứ tự NV', 'Mã trường', 'Tên trường', 'Mã xét tuyển', 'Tên mã xét tuyển', 'Nguyện vọng tuyển thẳng(điều 8)'];
    const sampleData = ['012345678901', '1', 'SGD', 'TRƯỜNG ĐẠI HỌC SÀI GÒN', '7140217', 'Sư phạm Ngữ văn', ''];
    const csvContent = headers.join(',') + '\n' + sampleData.join(',');
    const blob = new Blob(["\ufeff", csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", "Template_DanhSachNguyenVong.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const admittedCount = admissionResults.filter(
    (r) => r.trangThai === 'Đậu'
  ).length;
  const handleSaveResults = async () => {
    try {
      const payload: any[] = [];

      // Duyệt qua tất cả nguyện vọng hệ thống đang quản lý
      preferences.forEach(p => {
        // Tìm kết quả xét tuyển đối chiếu với nguyện vọng này
        const result = admissionResults.find(r => r.cccd === p.cccd);

        let ketQua = 'KHONG_TRUNG_TUYEN';
        let diemThi = 0;
        let diemCong = 0;
        let tongDiem = 0;

        if (result) {
          diemThi = result.diem;
          diemCong = result.diemCong;
          tongDiem = result.tongDiem;
          if (result.trangThai === 'Đậu' && result.nganhTrungTuyen === p.maNganh) {
            ketQua = 'TRUNG_TUYEN';
          }
        }

        payload.push({
          cccd: p.cccd,
          maNganh: p.maNganh,
          ketQua,
          diemThi,
          diemCong,
          tongDiem
        });
      });

      await admissionService.saveResults(payload);
      alert('Đã lưu kết quả thành công vào Database!');
    } catch (e: any) {
      console.error(e);
      alert(`Debug Lỗi: ${e.message} - ${e.response ? JSON.stringify(e.response.data) : 'No response data'}`);
    }
  };

  const [cccdError, setCccdError] = useState('');

  const handleCccdBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const val = e.target.value.trim();
    if (!val) {
      setCccdError('');
      return;
    }
    const cand = candidates.find(c => c.cccd === val);
    if (cand) {
      setFormData(prev => ({ ...prev, hoTen: cand.hoTen || ((cand as any).ho + ' ' + (cand as any).ten) }));
      setCccdError('');
    } else {
      setFormData(prev => ({ ...prev, hoTen: '' }));
      setCccdError('Không tìm thấy thí sinh trong quản lý!');
    }
  };

  const availableCombos = formData.maNganh
    ? majorCombinations
      .filter((mc) => mc.maNganh === formData.maNganh)
      .map((mc) => subjectCombinations.find((c) => c.maToHop === mc.maToHop))
      .filter(Boolean)
    : subjectCombinations;

  const sortedPreferences = React.useMemo(() => {
    return [...preferences].sort((a, b) => {
      if (a.cccd === b.cccd) return a.thuTuNV - b.thuTuNV;
      return a.cccd.localeCompare(b.cccd);
    });
  }, [preferences]);

  const totalPages = Math.ceil(sortedPreferences.length / itemsPerPage);
  const paginatedPreferences = sortedPreferences.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const totalPagesResults = Math.ceil(admissionResults.length / itemsPerPage);
  const paginatedResults = admissionResults.slice(
    (currentPageResults - 1) * itemsPerPage,
    currentPageResults * itemsPerPage
  );

  return (
    <div className="p-8">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-slate-800 mb-2">Xét tuyển đa phương thức</h1>
          <p className="text-slate-600">Thực hiện xử lý điểm và kết quả qua bộ lọc nguyện vọng ưu tiên</p>
        </div>
        <div className="flex gap-4">
          <button
            onClick={handleSaveResults}
            disabled={!hasRun || admissionResults.length === 0}
            className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-400 text-white px-6 py-2 rounded-lg font-medium shadow transition-colors"
          >
            Lưu CSDL
          </button>
        </div>
      </div>

      {/* Preferences Section */}
      <div className="bg-white rounded-lg shadow-md border border-slate-200 mb-8">
        <div className="p-6 border-b border-slate-200 flex justify-between items-center">
          <h2 className="text-lg font-semibold text-slate-800">
            Danh sách nguyện vọng
          </h2>
          <div className="flex gap-3">
            <button
              onClick={() => setIsImportOpen(true)}
              className="flex items-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg text-sm font-medium transition-colors"
            >
              <UploadIcon size={18} />
              Import Excel
            </button>
            <button
              onClick={handleAddPref}
              className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
              <PlusIcon size={18} />
              Thêm nguyện vọng
            </button>
          </div>
        </div>

        <div className="overflow-x-auto max-h-96 relative min-h-[200px]">
          {isLoading ? (
            <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
              <Loader2 className="animate-spin text-blue-600" size={32} />
              <span className="ml-2 text-slate-600">Đang tải dữ liệu...</span>
            </div>
          ) : null}
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200 sticky top-0">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  CCCD
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Họ tên
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thứ tự NV
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Ngành
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Tổ hợp
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedPreferences.map((pref) =>
                <tr
                  key={pref.id}
                  className="hover:bg-slate-50 transition-colors">

                  <td className="px-6 py-3 text-sm text-slate-800">
                    {pref.cccd}
                  </td>
                  <td className="px-6 py-3 text-sm font-medium text-slate-800">
                    {pref.hoTen}
                  </td>
                  <td className="px-6 py-3 text-sm text-slate-800">
                    <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-blue-100 text-blue-800 font-bold text-xs">
                      {pref.thuTuNV}
                    </span>
                  </td>
                  <td className="px-6 py-3 text-sm text-slate-800">
                    {pref.maNganh}
                  </td>
                  <td className="px-6 py-3 text-sm text-slate-800">
                    {pref.maToHop}
                  </td>
                  <td className="px-6 py-3 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleEditPref(pref)}
                        className="p-1.5 text-blue-600 hover:bg-blue-50 rounded">

                        <EditIcon size={16} />
                      </button>
                      <button
                        onClick={() => handleDeletePref(pref.id)}
                        className="p-1.5 text-red-600 hover:bg-red-50 rounded">

                        <TrashIcon size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="p-4 border-t border-slate-200">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setCurrentPage}
              totalItems={sortedPreferences.length}
              itemsPerPage={itemsPerPage}
            />
          </div>
        )}
      </div>

      {/* Admission Run Section */}
      <div className="bg-white rounded-lg shadow-md border border-slate-200 p-6 mb-6">
        <h2 className="text-lg font-semibold text-slate-800 mb-4">
          Cấu hình xét tuyển
        </h2>

        <div className="flex flex-col sm:flex-row items-end gap-6">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Điểm sàn chung
            </label>
            <input
              type="number"
              step="0.1"
              value={diemSan}
              onChange={(e) => setDiemSan(e.target.value)}
              className="w-32 px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500" />

          </div>

          <button
            onClick={runAdmission}
            className="flex items-center gap-2 bg-orange-600 hover:bg-orange-700 text-white px-6 py-2.5 rounded-lg font-semibold transition-colors">

            <PlayIcon size={20} />
            CHẠY XÉT TUYỂN
          </button>
        </div>
      </div>

      {/* Results Section */}
      {hasRun &&
        <div className="bg-white rounded-lg shadow-md border border-slate-200">
          <div className="p-6 border-b border-slate-200">
            <h2 className="text-lg font-semibold text-slate-800">
              Kết quả xét tuyển
            </h2>
            <p className="text-sm text-slate-600 mt-1">
              <strong>{admittedCount}</strong> thí sinh trúng tuyển /{' '}
              <strong>{candidates.length}</strong> tổng thí sinh
            </p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    CCCD
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Thí sinh
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Điểm thi
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Điểm cộng
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Tổng điểm
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Ngành trúng tuyển
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Trạng thái
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {paginatedResults.map((result, i) => (
                  <tr key={i} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4 text-sm text-slate-800">
                      {result.cccd}
                    </td>
                    <td className="px-6 py-4 text-sm font-medium text-slate-800">
                      {result.hoTen}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-800">
                      {result.diem.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 text-sm text-green-600">
                      +{result.diemCong.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 text-sm font-bold text-blue-600">
                      {result.tongDiem.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-800">
                      {result.nganhTrungTuyen}
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <div className="flex items-center gap-2">
                        {result.trangThai === 'Đậu' ? (
                          <>
                            <CheckCircleIcon className="text-green-600" size={18} />
                            <span className="text-green-600 font-medium">Đậu</span>
                          </>
                        ) : (
                          <>
                            <XCircleIcon className="text-red-600" size={18} />
                            <span className="text-red-600 font-medium">Không đậu</span>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {totalPagesResults > 1 && (
            <div className="p-4 border-t border-slate-200">
              <Pagination
                currentPage={currentPageResults}
                totalPages={totalPagesResults}
                onPageChange={setCurrentPageResults}
                totalItems={admissionResults.length}
                itemsPerPage={itemsPerPage}
              />
            </div>
          )}
        </div>
      }

      {/* Preference Modal */}
      {isModalOpen &&
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
            <div className="p-6 border-b border-slate-200">
              <h2 className="text-xl font-bold text-slate-800">
                {editingPref ? 'Chỉnh sửa nguyện vọng' : 'Thêm nguyện vọng'}
              </h2>
            </div>

            <form onSubmit={handleSubmitPref} className="p-6">
              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    CCCD
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.cccd}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        cccd: e.target.value
                      })
                    }
                    onBlur={handleCccdBlur}
                    className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 ${cccdError ? 'border-red-500 focus:ring-red-500' : 'border-slate-300 focus:ring-blue-500'}`} />
                  {cccdError && <p className="text-xs text-red-500 mt-1">{cccdError}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Họ tên
                  </label>
                  <input
                    type="text"
                    required
                    readOnly
                    value={formData.hoTen}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        hoTen: e.target.value
                      })
                    }
                    className="w-full px-3 py-2 border border-slate-300 bg-slate-50 cursor-not-allowed rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />

                </div>
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Thứ tự nguyện vọng
                </label>
                <input
                  type="number"
                  min="1"
                  required
                  value={formData.thuTuNV}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      thuTuNV: e.target.value
                    })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />

              </div>

              <div className="grid grid-cols-2 gap-4 mb-6">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Ngành
                  </label>
                  <select
                    required
                    value={formData.maNganh}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        maNganh: e.target.value,
                        maToHop: '' // reset toHop when Nganh changes
                      })
                    }
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">

                    <option value="">Chọn ngành</option>
                    {majors.map((m) =>
                      <option key={m.id} value={m.maNganh}>
                        {m.maNganh}
                      </option>
                    )}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Tổ hợp
                  </label>
                  <select
                    required
                    value={formData.maToHop}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        maToHop: e.target.value
                      })
                    }
                    disabled={!formData.maNganh}
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-slate-100 disabled:cursor-not-allowed">

                    <option value="">Chọn tổ hợp</option>
                    {availableCombos.map((c: any) =>
                      <option key={c.id} value={c.maToHop}>
                        {c.maToHop}
                      </option>
                    )}
                  </select>
                </div>
              </div>

              <div className="flex justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 transition-colors">

                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={!!cccdError}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white rounded-lg transition-colors">

                  Lưu
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <ImportModal
        isOpen={isImportOpen}
        onClose={() => setIsImportOpen(false)}
        onImport={handleImport}
        onDownloadTemplate={handleDownloadTemplate}
        title="Import danh sách nguyện vọng xét tuyển"
      />
    </div>);

}