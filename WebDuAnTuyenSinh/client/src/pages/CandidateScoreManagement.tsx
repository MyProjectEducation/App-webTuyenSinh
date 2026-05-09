import React, { useMemo, useState, useEffect } from 'react';
import { useAppContext } from '../context/AppContext';
import { scoreService } from '../services/scoreService';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon, Loader2, SaveIcon } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import Pagination from '../components/Pagination';

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
  const totalItems = filteredScores.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
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

  const fetchScores = async () => {
    try {
      const data = await scoreService.getAll();
      setCandidateScores(data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleImport = async (file: File) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const result = await scoreService.importScores(formData);
      alert(result.message);
      
      fetchScores();
      setIsImportOpen(false);
    } catch (error: any) {
      console.error('Lỗi import:', error);
      alert(error.response?.data?.message || 'Lỗi khi import file Excel');
    }
  };

  const handleDownloadTemplate = () => {
    const headers = ['CCCD', 'Phương thức', 'Toán', 'Vật lí', 'Hóa học', 'Sinh học', 'Lịch sử', 'Địa lí', 'Ngữ văn', 'Ngoại ngữ (Thi)', 'Ngoại ngữ (Quy đổi)', 'Công nghệ (CN)', 'Công nghệ (NN)', 'Tin học', 'GDKT & PL', 'ĐGNL', 'Năng khiếu 1', 'Năng khiếu 2'];
    const sampleData = ['012345678901', 'THPT', '8.5', '7.0', '8.0', '', '', '', '6.5', '7.5', '', '', '', '', '', '', '', ''];
    const csvContent = headers.join(',') + '\n' + sampleData.join(',');
    const blob = new Blob(["\ufeff", csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", "Template_DiemThiSinh.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
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

        <Pagination 
          currentPage={currentPage} 
          totalPages={totalPages}
          totalItems={totalItems} 
          itemsPerPage={itemsPerPage} 
          onPageChange={setCurrentPage} 
        />
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-5xl max-h-[95vh] overflow-y-auto overflow-x-hidden flex flex-col">
            
            <div className="p-6 border-b border-slate-100 sticky top-0 bg-white/95 backdrop-blur-sm z-10 flex justify-between items-center">
              <div>
                <h2 className="text-2xl font-bold text-slate-800">Cập nhật Điểm Thí sinh (Master-Detail Grid)</h2>
                <p className="text-sm text-slate-500 mt-1">Quản lý nguyên khối toàn bộ điểm của một thí sinh</p>
              </div>
              <button onClick={() => setIsModalOpen(false)} className="p-2 hover:bg-slate-100 rounded-full transition-colors">
                 <svg className="w-6 h-6 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"></path></svg>
              </button>
            </div>

            <div className="p-6 md:p-8 flex-1">
              {/* 1. MASTER HEADER */}
              <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 border-b border-slate-100 pb-6 gap-6">
                <div className="w-full md:w-1/2">
                   <label className="block text-sm font-semibold text-slate-700 mb-2">Tìm kiếm & Chọn Thí sinh *</label>
                   <div className="relative">
                      <input 
                        type="text" 
                        required 
                        list="candidate-cccd-list"
                        placeholder="Nhập CCCD hoặc Số báo danh..."
                        value={selectedCccd} 
                        onChange={(e) => setSelectedCccd(e.target.value)} 
                        className="w-full px-4 py-3 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all font-medium text-slate-800" 
                      />
                      <datalist id="candidate-cccd-list">
                        {candidates.map(c => (
                          <option key={c.id} value={c.cccd}>{c.ho} {c.ten} (SBD: {c.soBaoDanh})</option>
                        ))}
                      </datalist>
                   </div>
                   {candidateInfo && (
                      <div className="mt-3 inline-flex items-center px-3 py-1 rounded-full bg-blue-50 border border-blue-100 text-blue-700 text-sm font-medium">
                        <svg className="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
                        {candidateInfo.ho} {candidateInfo.ten}
                      </div>
                   )}
                </div>
                
                {/* TABS */}
                <div className="flex space-x-1 mt-4 md:mt-0 bg-slate-100 p-1.5 rounded-xl w-full md:w-auto overflow-x-auto">
                  {(['THPT', 'VSAT', 'DGNL'] as ScoreType[]).map(tab => (
                    <button 
                      type="button"
                      key={tab}
                      onClick={() => setMasterLoaiDiem(tab)}
                      className={`px-6 py-2.5 rounded-lg font-medium text-sm transition-all whitespace-nowrap ${masterLoaiDiem === tab ? 'bg-white text-blue-600 shadow-sm' : 'text-slate-500 hover:text-slate-800 hover:bg-slate-200/50'}`}
                    >
                      {tab === 'VSAT' ? 'V-SAT' : tab}
                    </button>
                  ))}
                </div>
              </div>

              {/* 2. DETAIL GRID */}
              <div className="mb-4">
                {masterLoaiDiem === 'DGNL' ? (
                  <div className="max-w-md mx-auto">
                    {Object.entries(mapToRender).map(([col, label]) => (
                      <div key={col} className="bg-orange-50 border border-orange-200 p-6 rounded-2xl text-center">
                         <label className="block text-sm font-bold text-orange-800 uppercase tracking-widest mb-4">{label}</label>
                         <input
                           type="number"
                           step="0.01"
                           placeholder="Tối đa 1200"
                           value={gridScores[col] || ''}
                           onChange={(e) => {
                             let val = parseFloat(e.target.value);
                             if (val > 1200) val = 1200;
                             if (val < 0) val = 0;
                             setGridScores({ ...gridScores, [col]: e.target.value ? val : '' });
                           }}
                           className="w-full max-w-[200px] text-center px-4 py-4 border-2 border-orange-300 rounded-xl focus:outline-none focus:ring-4 focus:ring-orange-500/20 focus:border-orange-500 text-3xl font-black text-orange-900 transition-all shadow-inner bg-white"
                         />
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {Object.entries(mapToRender).map(([col, label]) => (
                       <div key={col} className="flex flex-col">
                         <label className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">{label}</label>
                         <input 
                           type="number" 
                           step="0.01"
                           value={gridScores[col] || ''}
                           onChange={(e) => {
                             let val = parseFloat(e.target.value);
                             const maxLimit = masterLoaiDiem === 'VSAT' ? 150 : 10;
                             if (val > maxLimit) val = maxLimit;
                             if (val < 0) val = 0;
                             setGridScores({ ...gridScores, [col]: e.target.value ? val : '' });
                           }}
                           className="px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all font-semibold text-slate-800"
                           placeholder={masterLoaiDiem === 'VSAT' ? '0 - 150' : '0 - 10'}
                         />
                       </div>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* 3. FOOTER ACTION */}
            <div className="p-6 border-t border-slate-100 bg-slate-50 sticky bottom-0 z-10 rounded-b-2xl flex justify-end gap-3">
               <button 
                 onClick={() => setIsModalOpen(false)}
                 className="px-6 py-3 font-medium text-slate-600 hover:bg-slate-200 rounded-xl transition-colors"
               >
                 Hủy bỏ
               </button>
               <button 
                 onClick={handleSubmit}
                 className="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-xl font-semibold shadow-sm transition-colors flex items-center gap-2"
               >
                 <SaveIcon size={20} />
                 Lưu Toàn Bộ Điểm
               </button>
            </div>

          </div>
        </div>
      )}

      <ImportModal 
        isOpen={isImportOpen} 
        onClose={() => setIsImportOpen(false)} 
        onImport={handleImport} 
        onDownloadTemplate={handleDownloadTemplate}
        title="Import điểm thí sinh" 
      />
    </div>
  );
}