import React, { useState } from 'react';
import {
  useAppContext,
  AdmissionResult,
  Preference } from
'../context/AppContext';
import {
  PlayIcon,
  CheckCircleIcon,
  XCircleIcon,
  PlusIcon,
  TrashIcon,
  EditIcon } from
'lucide-react';
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
    subjectCombinations
  } = useAppContext();
  const [diemSan, setDiemSan] = useState('18');
  const [hasRun, setHasRun] = useState(false);
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
  // Calculate total score for a candidate based on a specific subject combination
  const calculateTotalScore = (cccd: string, maToHop: string) => {
    const combo = subjectCombinations.find((c) => c.maToHop === maToHop);
    if (!combo) return 0;
    // Get scores for this candidate
    const scores = candidateScores.filter((s) => s.cccd === cccd);
    // Find scores for the 3 subjects in the combination
    // Note: This is a simplified logic. In reality, you'd need to handle different score types (THPT, DGNL) properly
    const s1 = scores.find((s) => s.mon === combo.mon1)?.diem || 0;
    const s2 = scores.find((s) => s.mon === combo.mon2)?.diem || 0;
    const s3 = scores.find((s) => s.mon === combo.mon3)?.diem || 0;
    return s1 + s2 + s3;
  };
  const getBonusScore = (cccd: string) => {
    return bonusPoints.
    filter((p) => p.cccd === cccd).
    reduce((sum, p) => sum + p.diem, 0);
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
      const bonus = getBonusScore(cccd);
      // Calculate best score across all their preferences
      let bestScore = 0;
      candidatePrefs.forEach((pref) => {
        const baseScore = calculateTotalScore(cccd, pref.maToHop);
        if (baseScore > bestScore) bestScore = baseScore;
      });
      return {
        candidate,
        prefs: candidatePrefs,
        baseScore: bestScore,
        bonusScore: bonus,
        totalScore: bestScore + bonus
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
        if (major && major.admitted < major.quota) {
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
  const handleDeletePref = (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa nguyện vọng này?')) {
      setPreferences(preferences.filter((p) => p.id !== id));
    }
  };
  const handleSubmitPref = (e: React.FormEvent) => {
    e.preventDefault();
    if (editingPref) {
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
      const newPref: Preference = {
        id: Date.now().toString(),
        ...formData,
        thuTuNV: parseInt(formData.thuTuNV)
      };
      setPreferences([...preferences, newPref]);
    }
    setIsModalOpen(false);
  };
  const admittedCount = admissionResults.filter(
    (r) => r.trangThai === 'Đậu'
  ).length;
  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Nguyện vọng & Xét tuyển
        </h1>
        <p className="text-slate-600">
          Quản lý nguyện vọng đăng ký và chạy thuật toán xét tuyển
        </p>
      </div>

      {/* Preferences Section */}
      <div className="bg-white rounded-lg shadow-md border border-slate-200 mb-8">
        <div className="p-6 border-b border-slate-200 flex justify-between items-center">
          <h2 className="text-lg font-semibold text-slate-800">
            Danh sách nguyện vọng
          </h2>
          <button
            onClick={handleAddPref}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
            
            <PlusIcon size={18} />
            Thêm nguyện vọng
          </button>
        </div>

        <div className="overflow-x-auto max-h-96">
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
              {preferences.
              sort((a, b) => {
                if (a.cccd === b.cccd) return a.thuTuNV - b.thuTuNV;
                return a.cccd.localeCompare(b.cccd);
              }).
              map((pref) =>
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
                {admissionResults.map((result, i) =>
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
                        {result.trangThai === 'Đậu' ?
                    <>
                            <CheckCircleIcon
                        className="text-green-600"
                        size={18} />
                      
                            <span className="text-green-600 font-medium">
                              Đậu
                            </span>
                          </> :

                    <>
                            <XCircleIcon className="text-red-600" size={18} />
                            <span className="text-red-600 font-medium">
                              Không đậu
                            </span>
                          </>
                    }
                      </div>
                    </td>
                  </tr>
              )}
              </tbody>
            </table>
          </div>
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
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Họ tên
                  </label>
                  <input
                  type="text"
                  required
                  value={formData.hoTen}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    hoTen: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                
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
                    maNganh: e.target.value
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
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                  
                    <option value="">Chọn tổ hợp</option>
                    {subjectCombinations.map((c) =>
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
                className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors">
                
                  Lưu
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>);

}