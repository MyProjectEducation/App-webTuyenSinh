import React, { useState, useMemo } from 'react';
import { useAppContext, BonusPoint } from '../context/AppContext';
import { bonusService } from '../services/bonusService';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon, Loader2, SaveIcon } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import { Pagination } from '../components/Pagination';

export function BonusPointManagement() {
  const { 
    bonusPoints, setBonusPoints, 
    candidates, majors, majorCombinations, subjectCombinations, 
    preferences, candidateScores,
    isLoading 
  } = useAppContext();
  
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 15;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);

  // Form State
  const [cccd, setCccd] = useState('');
  const [manganh, setManganh] = useState('');
  const [matohop, setMatohop] = useState('');
  const [phuongthuc, setPhuongthuc] = useState('THPT');
  const [khuvuc, setKhuvuc] = useState('KV3');
  const [doituong, setDoituong] = useState('NONE');
  const [chungchi, setChungchi] = useState('NONE');

  // Pagination
  const paginatedPoints = bonusPoints.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const stats = useMemo(() => {
    return {
      count: bonusPoints.length,
      avg: bonusPoints.length > 0 ? (bonusPoints.reduce((acc, cur) => acc + cur.diem, 0) / bonusPoints.length).toFixed(2) : 0,
      thptCount: bonusPoints.filter(p => p.phuongThuc === 'THPT').length,
    };
  }, [bonusPoints]);

  const candidateInfo = useMemo(() => {
    const search = String(cccd).trim();
    if (!search) return null;
    return candidates.find(c => String(c.cccd).trim() === search || String(c.soBaoDanh).trim() === search);
  }, [cccd, candidates]);

  React.useEffect(() => {
    if (candidateInfo) {
      // Auto-fill khuvuc and doituong
      if (candidateInfo.khuVuc) setKhuvuc(candidateInfo.khuVuc);
      if (candidateInfo.doiTuong) setDoituong(candidateInfo.doiTuong);
      
      // Auto-select phuongthuc based on candidate's scores
      const cScores = candidateScores.filter(s => s.cccd === candidateInfo.cccd);
      if (cScores.length > 0) {
        // Find if they have VSAT or DGNL, prefer those over THPT
        const methods = new Set(cScores.map(s => s.loaiDiem));
        if (methods.has('VSAT')) setPhuongthuc('VSAT');
        else if (methods.has('DGNL')) setPhuongthuc('DGNL');
        else if (methods.has('THPT')) setPhuongthuc('THPT');
      }
    }
  }, [candidateInfo, candidateScores]);

  // Filter majors to ONLY those the candidate preferred
  const validMajors = useMemo(() => {
    if (!candidateInfo) return majors;
    const cPrefs = preferences.filter(p => p.cccd === candidateInfo.cccd).map(p => p.maNganh);
    if (cPrefs.length === 0) return majors; // fallback to all if no prefs yet
    // Unique list
    const uniquePrefs = Array.from(new Set(cPrefs));
    return majors.filter(m => uniquePrefs.includes(m.maNganh));
  }, [candidateInfo, preferences, majors]);

  const validCombos = useMemo(() => {
    if (!manganh) return [];
    const validMaToHop = majorCombinations.filter(mc => mc.maNganh === manganh).map(mc => mc.maToHop);
    return subjectCombinations.filter(sc => validMaToHop.includes(sc.maToHop));
  }, [manganh, majorCombinations, subjectCombinations]);

  const handleAdd = () => {
    setCccd('');
    setManganh('');
    setMatohop('');
    setPhuongthuc('THPT');
    setKhuvuc('KV3');
    setDoituong('NONE');
    setChungchi('NONE');
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa hệ thống điểm cộng ngữ cảnh này?')) {
      try {
        await bonusService.delete(id);
        setBonusPoints(bonusPoints.filter((p) => p.id !== id));
      } catch (err) {
        alert("Lỗi xóa dữ liệu");
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!candidateInfo) {
      alert("Mã CCCD không hợp lệ hoặc chưa tồn tại hệ thống thí sinh.");
      return;
    }

    try {
      const payload = {
        cccd, manganh, matohop, phuongthuc, khuvuc, doituong, chungchi
      };
      
      const result = await bonusService.create(payload) as any;
      
      const newDiemTong = result.diemTong || 0;
      const newDiemC = result.diemC || 0;
      const newDiemUt = result.diemUt || 0;
      const newGhiChu = result.ghiChu || '';
      
      const dc_keys = `${cccd}_${manganh}_${matohop}`;

      // Xóa điểm cũ (nếu trùng khóa dc_keys) và thêm điểm mới vào State thay vì refetch
      const filtered = bonusPoints.filter(p => p.id !== dc_keys && !(p.cccd === cccd && p.maNganh === manganh && p.maToHop === matohop));
      
      const newPoint: BonusPoint = {
        id: dc_keys,
        cccd: cccd,
        hoTen: `${candidateInfo.ho} ${candidateInfo.ten}`,
        maNganh: manganh,
        maToHop: matohop,
        phuongThuc: phuongthuc,
        loaiDiemCong: phuongthuc,
        diem: newDiemTong,
        diemC: newDiemC,
        diemUt: newDiemUt,
        ghiChu: newGhiChu
      };
      setBonusPoints([...filtered, newPoint]);
      setIsModalOpen(false);
    } catch (err: any) {
      alert(err.response?.data?.error || "Lỗi thao tác máy chủ");
    }
  };

  const handleImport = (data: any[]) => {
    // Left empty for brevity
  };

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">Quản lý điểm cộng xét tuyển</h1>
        <p className="text-slate-600">Quản lý điểm ưu tiên, chứng chỉ ngoại ngữ dựa trên ngữ cảnh Ngành & Tổ hợp & Phương thức</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Tổng số Hồ sơ điểm cộng (Records)</p>
          <p className="text-2xl font-bold text-slate-800">{stats.count}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Điểm cộng Trung bình</p>
          <p className="text-2xl font-bold text-slate-800">{stats.avg}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Đã áp dụng thang THPT</p>
          <p className="text-2xl font-bold text-slate-800">{stats.thptCount}</p>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-slate-800">Danh sách tổng hợp</h2>
          <div className="flex gap-3">
            <button onClick={() => setIsImportOpen(true)} className="flex items-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg font-medium transition-colors">
              <UploadIcon size={20} />
              Import
            </button>
            <button onClick={handleAdd} className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow">
              <PlusIcon size={20} />
              Thêm điểm cộng mới
            </button>
          </div>
        </div>

        <div className="overflow-x-auto relative min-h-[200px]">
          {isLoading && (
            <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
              <Loader2 className="animate-spin text-blue-600" size={32} />
              <span className="ml-2 text-slate-600">Đang tải dữ liệu...</span>
            </div>
          )}
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">CCCD</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Họ tên</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Ngữ cảnh (Ngành - Môn - PT)</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Tổng (+điểm)</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Chi tiết Ghi chú</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedPoints.map((point) => (
                <tr key={point.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-6 py-4 text-sm text-slate-800">{point.cccd}</td>
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">{point.hoTen}</td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    <div className="flex flex-col gap-1">
                      <span className="font-semibold text-blue-800">{point.maNganh}</span>
                      <div className="flex gap-2">
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-600">{point.maToHop}</span>
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-purple-100 text-purple-700">{point.phuongThuc}</span>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm font-bold text-green-600">+{point.diem}</td>
                  <td className="px-6 py-4 text-sm text-slate-500 italic max-w-xs truncate">{point.ghiChu}</td>
                  <td className="px-6 py-4 text-sm">
                    <button onClick={() => handleDelete(point.id)} className="p-2 text-red-600 hover:bg-red-50 rounded transition-colors" title="Xóa">
                      <TrashIcon size={18} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <Pagination currentPage={currentPage} totalItems={bonusPoints.length} itemsPerPage={itemsPerPage} onPageChange={setCurrentPage} />
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-slate-200 sticky top-0 bg-white z-10">
              <h2 className="text-xl font-bold text-slate-800">Thêm / Chỉnh sửa Cấu Hình Điểm Cộng</h2>
              <p className="text-sm text-slate-500 mt-1">Thông tin Quy đổi sẽ được hệ thống Master tự động tính toán dựa trên quy chế.</p>
            </div>

            <form onSubmit={handleSubmit} className="p-6 bg-slate-50">
              <div className="bg-white p-5 rounded-lg border border-slate-200 mb-6">
                <h3 className="font-semibold text-slate-700 mb-4 border-b pb-2">1. Định danh & Phương thức</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5 mb-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Mã CCCD thí sinh *</label>
                    <input 
                      type="text" required list="candidate-cccd-list" placeholder="Gõ CCCD..."
                      value={cccd} onChange={(e) => setCccd(e.target.value)} 
                      className="w-full px-3 py-2 border border-slate-300 rounded focus:border-blue-500 focus:ring-1 focus:ring-blue-500" 
                    />
                    <datalist id="candidate-cccd-list">
                      {candidates.map(c => <option key={c.id} value={c.cccd}>{c.ho} {c.ten}</option>)}
                    </datalist>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Họ tên thí sinh</label>
                    <div className="w-full px-3 py-2 bg-slate-100 border border-slate-200 rounded text-slate-600 font-semibold cursor-not-allowed">
                      {candidateInfo ? `${candidateInfo.ho} ${candidateInfo.ten}` : '--- Chưa chọn hợp lệ ---'}
                    </div>
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Phương thức Xét tuyển *</label>
                  <select required value={phuongthuc} onChange={(e) => setPhuongthuc(e.target.value)} className="w-full px-3 py-2 border border-slate-300 rounded focus:border-blue-500">
                    <option value="THPT">Thang THPT (Trần 3.0đ)</option>
                    <option value="VSAT">Thang V-SAT (Trần 45.0đ)</option>
                    <option value="DGNL">Thang ĐGNL (Trần 120.0đ)</option>
                  </select>
                </div>
              </div>

              <div className="bg-white p-5 rounded-lg border border-slate-200 mb-6">
                <h3 className="font-semibold text-slate-700 mb-4 border-b pb-2">2. Ngữ cảnh (Nguyện vọng Đăng ký)</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Ngành xét tuyển *</label>
                    <select required value={manganh} onChange={(e) => setManganh(e.target.value)} className="w-full px-3 py-2 border border-slate-300 rounded focus:border-blue-500">
                      <option value="">-- Chọn ngành --</option>
                      {validMajors.map(m => <option key={m.id} value={m.maNganh}>{m.maNganh} - {m.tenNganh}</option>)}
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Tổ hợp môn xét tuyển *</label>
                    <select required value={matohop} onChange={(e) => setMatohop(e.target.value)} disabled={!manganh} className="w-full px-3 py-2 border border-slate-300 rounded focus:border-blue-500 disabled:bg-slate-100 disabled:text-slate-400">
                      <option value="">-- Chọn tổ hợp --</option>
                      {validCombos.map(c => <option key={c.id} value={c.maToHop}>{c.maToHop} - {c.tenToHop}</option>)}
                    </select>
                  </div>
                </div>
              </div>

              <div className="bg-white p-5 rounded-lg border border-slate-200 mb-6">
                <h3 className="font-semibold text-slate-700 mb-4 border-b pb-2">3. Đối tượng & Chứng chỉ Khuyến khích</h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Khu Vực Ưu Tiên</label>
                    <select value={khuvuc} onChange={(e) => setKhuvuc(e.target.value)} className="w-full px-3 py-2 border border-slate-300 rounded">
                      <option value="KV3">KV3 (0đ)</option>
                      <option value="KV2">KV2 (+0.25đ)</option>
                      <option value="KV2-NT">KV2-NT (+0.5đ)</option>
                      <option value="KV1">KV1 (+0.75đ)</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Đối Tượng Ưu Tiên</label>
                    <select value={doituong} onChange={(e) => setDoituong(e.target.value)} className="w-full px-3 py-2 border border-slate-300 rounded">
                      <option value="NONE">Không có</option>
                      <option value="DT01">ĐT 01 (+2.0đ)</option>
                      <option value="DT02">ĐT 02 (+2.0đ)</option>
                      <option value="DT03">ĐT 03 (+2.0đ)</option>
                      <option value="DT04">ĐT 04 (+2.0đ)</option>
                      <option value="DT05">ĐT 05 (+1.0đ)</option>
                      <option value="DT06">ĐT 06 (+1.0đ)</option>
                      <option value="DT07">ĐT 07 (+1.0đ)</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Chứng chỉ Tiếng Anh</label>
                    <select value={chungchi} onChange={(e) => setChungchi(e.target.value)} className="w-full px-3 py-2 border border-slate-300 rounded text-blue-700 bg-blue-50 font-semibold">
                      <option value="NONE">Chưa có chứng chỉ</option>
                      <option value="IELTS 5.5">IELTS 5.5 (QĐ 1.0đ)</option>
                      <option value="IELTS 6.0">IELTS 6.0 (QĐ 1.5đ)</option>
                      <option value="IELTS 7.0">IELTS ≥ 7.0 (QĐ 2.0đ)</option>
                    </select>
                  </div>
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t border-slate-200">
                <button type="button" onClick={() => setIsModalOpen(false)} className="px-5 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-100 transition-colors">
                  Đóng Form
                </button>
                <button type="submit" className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors shadow flex items-center gap-2">
                  <SaveIcon size={18} /> Lệnh Cập Nhật Hệ Thống Server
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
      <ImportModal isOpen={isImportOpen} onClose={() => setIsImportOpen(false)} onImport={handleImport} columns={['CCCD', 'Họ tên', 'Loại điểm cộng', 'Điểm', 'Ghi chú']} title="Import điểm cộng ưu tiên" />
    </div>
  );
}