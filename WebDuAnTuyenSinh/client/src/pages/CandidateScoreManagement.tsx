import React, { useMemo, useState, useEffect } from 'react';
import { useAppContext } from '../context/AppContext';
import { scoreService } from '../services/scoreService';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon, Loader2, SaveIcon } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import { Pagination } from '../components/Pagination';

type ScoreType = 'THPT' | 'VSAT' | 'DGNL';

const THPT_VSAT_MAP: Record<string, string> = {
  TO: 'Toán',
  LI: 'Lý',
  HO: 'Hóa',
  SI: 'Sinh',
  VA: 'Văn',
  SU: 'Sử',
  DI: 'Địa',
  N1_THI: 'Ngoại ngữ (gốc)',
  N1_CC: 'CCNN (QĐ)',
  CNCN: 'CN Công nghiệp',
  CNNN: 'CN Nông nghiệp',
  TI: 'Tin học',
  KTPL: 'Giáo dục KTPL',
  NK1: 'Năng khiếu 1',
  NK2: 'Năng khiếu 2'
};

const DGNL_MAP: Record<string, string> = {
  NL1: 'Điểm Tổng hợp'
};

export function CandidateScoreManagement() {
  const { candidateScores, setCandidateScores, candidates, isLoading } = useAppContext();
  const [activeTab, setActiveTab] = useState<ScoreType>('THPT');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 15;
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  
  // Master Detail Grid State
  const [selectedCccd, setSelectedCccd] = useState('');
  const [masterLoaiDiem, setMasterLoaiDiem] = useState<ScoreType>('THPT');
  const [gridScores, setGridScores] = useState<Record<string, string | number>>({});

  // Cập nhật Grid từ CSDL bộ nhớ bất cứ khi nào đổi Thí sinh hoặc Phương thức
  useEffect(() => {
    if (!selectedCccd) {
      setGridScores({});
      return;
    }
    const currentScoresForCand = candidateScores.filter(
      s => s.cccd === selectedCccd && s.loaiDiem === masterLoaiDiem
    );
    const newGrid: Record<string, string | number> = {};
    const mapToUse = masterLoaiDiem === 'DGNL' ? DGNL_MAP : THPT_VSAT_MAP;
    
    // Gán rỗng toàn bộ
    Object.keys(mapToUse).forEach(col => newGrid[col] = '');
    
    // Đổ dữ liệu đang có vào Grid
    currentScoresForCand.forEach(s => {
      if (s.colName) {
        newGrid[s.colName] = s.diem;
      }
    });

    setGridScores(newGrid);
  }, [selectedCccd, masterLoaiDiem, candidateScores]);

  // Filter scores by active tab (for display)
  const filteredScores = candidateScores.filter((s) => s.loaiDiem === activeTab);
  const paginatedScores = filteredScores.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const stats = useMemo(() => {
    const currentScores = filteredScores;
    const count = currentScores.length;
    const avg = count > 0 ? currentScores.reduce((sum, s) => sum + s.diem, 0) / count : 0;
    const bySubject = currentScores.reduce((acc, s) => {
        if (!acc[s.mon]) acc[s.mon] = { total: 0, count: 0 };
        acc[s.mon].total += s.diem;
        acc[s.mon].count += 1;
        return acc;
      }, {} as Record<string, { total: number; count: number; }>
    );
    return { count, avg, bySubject };
  }, [filteredScores]);

  const handleAddOrEdit = (presetCccd = '', presetDiemType: ScoreType = activeTab) => {
    setSelectedCccd(presetCccd);
    setMasterLoaiDiem(presetDiemType);
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa điểm môn này không?')) {
      try {
        await scoreService.delete(id);
        setCandidateScores(candidateScores.filter((s) => s.id !== id));
      } catch (err) {
        alert("Lỗi xóa điểm");
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedCccd) {
      alert("Vui lòng chọn hoặc nhập mã CCCD thí sinh!");
      return;
    }

    // Clean payload (lật bỏ các số rỗng)
    const payloadScores: Record<string, number | null> = {};
    const mapToUse = masterLoaiDiem === 'DGNL' ? DGNL_MAP : THPT_VSAT_MAP;
    
    Object.keys(mapToUse).forEach(col => {
      const val = gridScores[col];
      payloadScores[col] = (val !== '' && val !== undefined) ? parseFloat(val.toString()) : null;
    });

    try {
      const payload = {
        cccd: selectedCccd,
        loaiDiem: masterLoaiDiem,
        scores: payloadScores
      };
      
      await scoreService.create(payload); // Upsert API
      
      // Sync lại Context (Cách sạch nhất: Lấy về list API, nhưng tạm thời Sync Context Local)
      let syncedScores = candidateScores.filter(s => !(s.cccd === selectedCccd && s.loaiDiem === masterLoaiDiem));
      const hoTenCand = candidates.find(c => c.cccd === selectedCccd)?.ho + ' ' + candidates.find(c => c.cccd === selectedCccd)?.ten;

      Object.entries(payloadScores).forEach(([col, val]) => {
        if (val !== null && val > 0) {
          syncedScores.push({
            id: `${selectedCccd}_${masterLoaiDiem}_${col}`,
            cccd: selectedCccd,
            hoTen: hoTenCand && hoTenCand.trim() !== 'undefined undefined' ? hoTenCand : 'Thí sinh vô danh',
            loaiDiem: masterLoaiDiem,
            mon: mapToUse[col] || col,
            colName: col,
            diem: val
          });
        }
      });
      setCandidateScores(syncedScores);
      setIsModalOpen(false);
    } catch (err: any) {
      const errorMsg = err.response?.data?.error || "Lỗi lưu điểm";
      alert(errorMsg);
    }
  };

  const handleImport = (data: any[]) => {
    // Để giữ tinh giản code import
  };

  // Find candidate name logic
  const candidateInfo = useMemo(() => {
    if (!selectedCccd) return null;
    return candidates.find(c => c.cccd === selectedCccd || c.soBaoDanh === selectedCccd);
  }, [selectedCccd, candidates]);

  const mapToRender = masterLoaiDiem === 'DGNL' ? DGNL_MAP : THPT_VSAT_MAP;

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">Quản lý điểm thí sinh</h1>
        <p className="text-slate-600">Quản lý nhanh điểm thi THPT, ĐGNL, và VSAT theo cơ chế lưới (Grid Upsert)</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Tổng số đầu điểm ({activeTab})</p>
          <p className="text-2xl font-bold text-slate-800">{stats.count}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Điểm trung bình</p>
          <p className="text-2xl font-bold text-slate-800">{stats.avg.toFixed(2)}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Số lượng thí sinh đã nộp điểm</p>
          <p className="text-2xl font-bold text-slate-800">
            {new Set(filteredScores.map(s => s.cccd)).size}
          </p>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-4 border-b border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex bg-slate-100 p-1 rounded-lg">
            {(['THPT', 'VSAT', 'DGNL'] as ScoreType[]).map((type) => (
              <button
                key={type}
                onClick={() => {
                  setActiveTab(type);
                  setCurrentPage(1);
                }}
                className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${activeTab === type ? 'bg-white text-blue-600 shadow-sm' : 'text-slate-600 hover:text-slate-800'}`}
              >
                Kỳ thi {type}
              </button>
            ))}
          </div>

          <div className="flex gap-3">
            <button
              onClick={() => setIsImportOpen(true)}
              className="flex items-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg text-sm font-medium transition-colors"
            >
              <UploadIcon size={18} />
              Import
            </button>
            <button
              onClick={() => handleAddOrEdit('', activeTab)}
              className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
            >
              <PlusIcon size={18} />
              Chỉnh sửa / Thêm cụm điểm (Grid)
            </button>
          </div>
        </div>

        <div className="overflow-x-auto relative min-h-[200px]">
          {isLoading ? (
            <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
              <Loader2 className="animate-spin text-blue-600" size={32} />
              <span className="ml-2 text-slate-600">Đang tải dữ liệu...</span>
            </div>
          ) : null}
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">CCCD</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Họ tên</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Môn</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Điểm</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedScores.map((score) => (
                <tr key={score.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-6 py-4 text-sm text-slate-800">{score.cccd}</td>
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">{score.hoTen}</td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    <span className="bg-blue-50 text-blue-700 px-2 py-1 rounded font-medium">{score.mon}</span>
                  </td>
                  <td className="px-6 py-4 text-sm font-semibold text-red-600">{score.diem}</td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex items-center gap-2">
                      {/* Sửa cụm điểm của Thí Sinh ngay */}
                      <button
                        onClick={() => handleAddOrEdit(score.cccd, score.loaiDiem)}
                        className="p-2 text-blue-600 hover:bg-blue-50 rounded transition-colors"
                        title="Mở Grid Cập nhật toàn bộ điểm"
                      >
                        <EditIcon size={18} />
                      </button>
                      <button
                        onClick={() => handleDelete(score.id)}
                        className="p-2 text-red-600 hover:bg-red-50 rounded transition-colors"
                        title="Xóa riêng điểm nầy"
                      >
                        <TrashIcon size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {paginatedScores.length === 0 && (
                <tr>
                  <td colSpan={5} className="px-6 py-8 text-center text-slate-500">
                    Chưa có dữ liệu điểm {activeTab}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <Pagination currentPage={currentPage} totalItems={filteredScores.length} itemsPerPage={itemsPerPage} onPageChange={setCurrentPage} />
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-slate-200 sticky top-0 bg-white z-10 flex justify-between items-center">
              <div>
                <h2 className="text-xl font-bold text-slate-800">Quản lý Cụm điểm Thí sinh (Master-Detail Grid)</h2>
                <p className="text-sm text-slate-500">Cập nhật hàng loạt điểm số của một học sinh trong duy nhất 1 thao tác Lưu</p>
              </div>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
              {/* VÙNG Định danh Master */}
              <div className="bg-slate-50 p-5 rounded-lg border border-slate-200 mb-6">
                <h3 className="font-semibold text-slate-700 mb-4">Thông tin Định danh (Master)</h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div className="relative">
                    <label className="block text-sm font-medium text-slate-700 mb-1">Mã CCCD thí sinh *</label>
                    <input 
                      type="text" 
                      required 
                      list="candidate-cccd-list"
                      placeholder="Gõ CCCD để tìm kiếm..."
                      value={selectedCccd} 
                      onChange={(e) => setSelectedCccd(e.target.value)} 
                      className="w-full px-3 py-2 border border-blue-400 bg-blue-50 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-medium text-blue-900" 
                    />
                    <datalist id="candidate-cccd-list">
                      {candidates.map(c => (
                        <option key={c.id} value={c.cccd}>{c.ho} {c.ten} (SBD: {c.soBaoDanh})</option>
                      ))}
                    </datalist>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Họ Tên Thí sinh</label>
                    <div className="w-full px-3 py-2 bg-slate-200 border border-slate-300 rounded-lg text-slate-600 font-semibold truncate cursor-not-allowed">
                      {candidateInfo ? `${candidateInfo.ho} ${candidateInfo.ten}` : '--- Chưa chọn hợp lệ ---'}
                    </div>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Phương thức xét (Tab)</label>
                    <select 
                      value={masterLoaiDiem} 
                      onChange={(e) => setMasterLoaiDiem(e.target.value as ScoreType)} 
                      className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-medium"
                    >
                      <option value="THPT">Kết Quả THPT</option>
                      <option value="VSAT">Kết Quả V-SAT</option>
                      <option value="DGNL">Điểm ĐGNL</option>
                    </select>
                  </div>
                </div>
              </div>

              {/* VÙNG Điểm Chi Tiết Detail */}
              <div className="mb-6">
                <h3 className="font-semibold text-slate-700 mb-4 border-b pb-2">Bảng Điểm Môn thi (Grid Record)</h3>
                
                {masterLoaiDiem === 'DGNL' ? (
                  // Layout cho ĐGNL
                  <div className="grid grid-cols-1 gap-6 max-w-sm">
                    {Object.entries(mapToRender).map(([col, label]) => (
                      <div key={col} className="bg-orange-50 border border-orange-200 p-4 rounded-lg">
                        <label className="block text-sm font-bold text-orange-800 mb-2">{label}</label>
                        <input
                          type="number"
                          step="0.01"
                          placeholder="VD: 850"
                          value={gridScores[col] || ''}
                          onChange={(e) => setGridScores({ ...gridScores, [col]: e.target.value })}
                          className="w-full px-4 py-3 border border-orange-300 rounded focus:outline-none focus:ring-2 focus:ring-orange-500 text-lg font-bold text-orange-900"
                        />
                      </div>
                    ))}
                  </div>
                ) : (
                  // Layout cho THPT / VSAT
                  <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                    {Object.entries(mapToRender).map(([col, label]) => (
                      <div key={col} className="flex flex-col">
                        <label className="block text-xs font-semibold text-slate-600 mb-1">{label}</label>
                        <input
                          type="number"
                          step="0.01"
                          placeholder="---"
                          value={gridScores[col] || ''}
                          onChange={(e) => setGridScores({ ...gridScores, [col]: e.target.value })}
                          className="w-full px-3 py-2 border border-slate-300 rounded focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 font-medium"
                        />
                      </div>
                    ))}
                  </div>
                )}
                
              </div>

              <div className="flex justify-end gap-3 pt-6 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 transition-colors"
                >
                  Hủy bỏ
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors shadow flex items-center gap-2"
                >
                  <SaveIcon size={18} />
                  Cập Nhật Toàn Bộ Điểm
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ImportModal isOpen={isImportOpen} onClose={() => setIsImportOpen(false)} onImport={handleImport} columns={['CCCD', 'Họ tên', 'Loại điểm', 'Môn', 'Điểm']} title="Import điểm thí sinh" />
    </div>
  );
}