import React, { useState, useMemo, useEffect } from 'react';
import { useAppContext, ConversionRule } from '../context/AppContext';
import { conversionService } from '../services/conversionService';
import { EditIcon, TrashIcon, PlusIcon, UploadIcon, SearchIcon, Loader2 } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import Pagination from '../components/Pagination';

export function ScoreConversion() {
  const { conversionRules, setConversionRules, isLoading } = useAppContext();
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 15;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [editingRule, setEditingRule] = useState<ConversionRule | null>(null);

  // Dynamic Form State
  const [formType, setFormType] = useState('NGOAI_NGU');

  // Fields
  const [phuongThuc, setPhuongThuc] = useState('IELTS');
  const [mon, setMon] = useState('N1');
  const [phanVi, setPhanVi] = useState('');
  const [diemA, setDiemA] = useState('');
  const [diemB, setDiemB] = useState('');
  const [diemC, setDiemC] = useState('');
  const [diemD, setDiemD] = useState('');

  // Auto-generate Mã Quy Đổi
  const maQuyDoi = useMemo(() => {
    if (formType === 'NGOAI_NGU') {
      return `${phuongThuc}_${diemA}`; // VD: IELTS_5.5
    } else {
      return `${phuongThuc}_${mon}_${phanVi}`; // VD: VSAT_TO_20
    }
  }, [formType, phuongThuc, mon, diemA, phanVi]);

  // Effects for form reset when Type changes
  useEffect(() => {
    if (!editingRule) {
      if (formType === 'NGOAI_NGU') {
        setPhuongThuc('IELTS');
        setMon('N1');
        setDiemC('');
        setDiemD('');
        setPhanVi('');
      } else {
        setPhuongThuc('VSAT');
        setMon('TO');
      }
    }
  }, [formType, editingRule]);

  const filteredRules = conversionRules.filter(
    (r) =>
      r.d_maquydoi.toLowerCase().includes(searchTerm.toLowerCase()) ||
      r.d_phuongthuc.toLowerCase().includes(searchTerm.toLowerCase()) ||
      r.d_mon.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalItems = filteredRules.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  const paginatedRules = filteredRules.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handleAdd = () => {
    setEditingRule(null);
    setFormType('NGOAI_NGU');
    setPhuongThuc('IELTS');
    setMon('N1');
    setDiemA('');
    setDiemB('');
    setDiemC('');
    setDiemD('');
    setPhanVi('');
    setIsModalOpen(true);
  };

  const handleEdit = (rule: ConversionRule) => {
    setEditingRule(rule);
    const isNgoaiNgu = rule.d_phuongthuc === 'IELTS' || rule.d_phuongthuc === 'TOEFL';
    setFormType(isNgoaiNgu ? 'NGOAI_NGU' : 'VSAT');
    
    setPhuongThuc(rule.d_phuongthuc);
    setMon(rule.d_mon || 'N1');
    setDiemA(rule.d_diema.toString());
    setDiemB(rule.d_diemb?.toString() || '');
    setDiemC(rule.d_diemc?.toString() || '');
    setDiemD(rule.d_diemd?.toString() || '');
    setPhanVi(rule.d_phanvi || '');
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa quy tắc quy đổi / nội suy này?')) {
      try {
        await conversionService.delete(id);
        setConversionRules(conversionRules.filter((r) => r.id !== id));
      } catch (err) {
        alert("Lỗi xóa quy tắc");
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const payload = {
      d_phuongthuc: phuongThuc,
      d_maquydoi: maQuyDoi,
      d_mon: mon,
      d_diema: parseFloat(diemA),
      d_diemb: diemB ? parseFloat(diemB) : undefined,
      d_diemc: formType === 'VSAT' && diemC ? parseFloat(diemC) : undefined,
      d_diemd: formType === 'VSAT' && diemD ? parseFloat(diemD) : undefined,
      d_phanvi: formType === 'VSAT' ? phanVi : undefined
    };

    try {
      if (editingRule) {
        await conversionService.update(editingRule.id, payload);
        setConversionRules(
          conversionRules.map((r) =>
            r.id === editingRule.id ? { ...r, ...payload, id: r.id } : r
          )
        );
      } else {
        const result = await conversionService.create(payload);
        const newRule: ConversionRule = {
          id: result.id,
          ...payload
        };
        setConversionRules([...conversionRules, newRule]);
      }
      setIsModalOpen(false);
    } catch (err) {
      alert("Lỗi lưu dữ liệu. Mã quy đổi có thể bị trùng!");
    }
  };

  const fetchRules = async () => {
    try {
      const data = await conversionService.getAll();
      setConversionRules(data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleImport = async (file: File) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const result = await conversionService.importConversions(formData);
      alert(result.message);
      
      fetchRules();
      setIsImportOpen(false);
    } catch (error: any) {
      console.error('Lỗi import:', error);
      alert(error.response?.data?.message || 'Lỗi khi import file Excel');
    }
  };

  const handleDownloadTemplate = () => {
    const headers = ['Mã quy đổi', 'Phương thức', 'Môn', 'Điểm a', 'Điểm b', 'Điểm c', 'Điểm d', 'Phân vị'];
    const sampleData = ['IELTS_6.0', 'IELTS', 'N1', '6.0', '10', '', '', ''];
    const csvContent = headers.join(',') + '\n' + sampleData.join(',');
    const blob = new Blob(["\ufeff", csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", "Template_BangQuyDoi.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Bảng Quy Phân & Nội Suy Đại Số
        </h1>
        <p className="text-slate-600">
          Thiết lập thuật toán tính d(x) điểm thi dựa trên (a,b) phân vị hoặc quy đổi chuẩn ngoại ngữ.
        </p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="w-full sm:w-80 relative">
              <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400" size={20} />
              <input
                type="text" placeholder="Tìm theo mã quy đổi, môn..." value={searchTerm}
                onChange={(e) => { setSearchTerm(e.target.value); setCurrentPage(1); }}
                className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
              />
            </div>
            <div className="flex gap-3 w-full sm:w-auto">
              <button onClick={() => setIsImportOpen(true)} className="flex-1 sm:flex-none flex items-center justify-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg font-medium transition-colors">
                <UploadIcon size={20} /> Import CSV
              </button>
              <button onClick={handleAdd} className="flex-1 sm:flex-none flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium shadow transition-colors">
                <PlusIcon size={20} /> Lập Quy tắc Mới
              </button>
            </div>
          </div>
        </div>

        <div className="overflow-x-auto relative min-h-[300px]">
          {isLoading ? (
            <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
              <Loader2 className="animate-spin text-blue-600" size={32} />
              <span className="ml-2 text-slate-600">Đang tải biểu thức...</span>
            </div>
          ) : null}
          <table className="w-full text-sm">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left font-semibold text-slate-600 uppercase">Mã (Code)</th>
                <th className="px-5 py-3 text-left font-semibold text-slate-600 uppercase">Hệ số Phân vị</th>
                <th className="px-5 py-3 text-center font-semibold text-blue-800 uppercase bg-blue-50">Khoảng Gốc (a → b)</th>
                <th className="px-5 py-3 text-center font-semibold text-emerald-800 uppercase bg-emerald-50">Khoảng Đích (c → d)</th>
                <th className="px-5 py-3 text-left font-semibold text-slate-600 uppercase">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedRules.map((rule) => {
                const isVSAT = rule.d_phuongthuc === 'VSAT';
                return (
                  <tr key={rule.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-bold text-slate-800">{rule.d_maquydoi}</div>
                      <div className="text-xs text-slate-500">{rule.d_phuongthuc} / Môn: {rule.d_mon}</div>
                    </td>
                    <td className="px-5 py-4 font-medium text-purple-700">{rule.d_phanvi || 'N/A'}</td>
                    
                    <td className="px-5 py-4 font-medium text-center bg-blue-50 bg-opacity-30">
                      {isVSAT ? (
                        <div className="flex items-center justify-center gap-2">
                          <span className="bg-white border rounded px-2 text-slate-600 text-xs">a: {rule.d_diema}</span>
                          <span className="text-slate-400">→</span>
                          <span className="bg-white border rounded px-2 text-slate-600 text-xs">b: {rule.d_diemb}</span>
                        </div>
                      ) : (
                        <span className="bg-white border rounded px-2 text-slate-600 text-xs shadow-sm">Gốc: {rule.d_diema}</span>
                      )}
                    </td>

                    <td className="px-5 py-4 font-medium text-center bg-emerald-50 bg-opacity-30">
                       {isVSAT ? (
                        <div className="flex items-center justify-center gap-2">
                          <span className="bg-white border-emerald-200 rounded px-2 text-emerald-700 text-xs">c: {rule.d_diemc}</span>
                          <span className="text-emerald-400">→</span>
                          <span className="bg-white border-emerald-200 rounded px-2 text-emerald-700 text-xs">d: {rule.d_diemd}</span>
                        </div>
                      ) : (
                        <span className="font-bold text-emerald-600">={rule.d_diemb} điểm</span>
                      )}
                    </td>

                    <td className="px-5 py-4">
                      <div className="flex items-center gap-2">
                        <button onClick={() => handleEdit(rule)} className="p-1.5 text-blue-600 hover:bg-blue-100 rounded">
                          <EditIcon size={16} />
                        </button>
                        <button onClick={() => handleDelete(rule.id)} className="p-1.5 text-red-600 hover:bg-red-100 rounded">
                          <TrashIcon size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
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
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto flex flex-col relative">
            <button onClick={() => setIsModalOpen(false)} className="absolute top-6 right-6 p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-full transition-colors z-20">
               <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"></path></svg>
            </button>

            <div className="p-8 pb-4">
              <h2 className="text-2xl font-bold text-slate-800 mb-1">
                {editingRule ? 'Biên dịch Quy tắc Toán học' : 'Khai báo Thuật toán Quy đổi'}
              </h2>
              <p className="text-sm text-slate-500">Hệ thống Auto-Generative ID đảm bảo không bao giờ trùng lặp Khóa Chính.</p>
            </div>

            <form onSubmit={handleSubmit} className="p-8 pt-4">
              {/* 1. ĐỘNG CƠ CHUYỂN ĐỔI */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
                <button 
                  type="button"
                  onClick={() => setFormType('NGOAI_NGU')}
                  className={`p-5 rounded-2xl border-2 text-left transition-all ${formType === 'NGOAI_NGU' ? 'border-blue-600 bg-blue-50 shadow-md shadow-blue-500/10' : 'border-slate-100 hover:border-slate-300 bg-white'}`}
                >
                  <div className={`font-bold text-lg ${formType === 'NGOAI_NGU' ? 'text-blue-800' : 'text-slate-700'}`}>🌍 Quy đổi Ngoại Ngữ</div>
                  <div className="text-sm text-slate-500 mt-1">Dành cho IELTS, TOEFL, VSTEP... (1 chiều)</div>
                </button>
                <button 
                  type="button"
                  onClick={() => setFormType('VSAT')}
                  className={`p-5 rounded-2xl border-2 text-left transition-all ${formType === 'VSAT' ? 'border-blue-600 bg-blue-50 shadow-md shadow-blue-500/10' : 'border-slate-100 hover:border-slate-300 bg-white'}`}
                >
                  <div className={`font-bold text-lg ${formType === 'VSAT' ? 'text-blue-800' : 'text-slate-700'}`}>📊 Nội suy V-SAT</div>
                  <div className="text-sm text-slate-500 mt-1">Áp dụng công thức tuyến tính 4 biến a, b, c, d</div>
                </button>
              </div>

              {/* 2. DYNAMIC FORM RENDERING */}
              {formType === 'NGOAI_NGU' ? (
                <div className="bg-slate-50 p-6 rounded-2xl border border-slate-100 space-y-6">
                   <div className="grid grid-cols-2 gap-6">
                     <div>
                       <label className="block text-sm font-bold text-slate-700 mb-2">Loại Chứng Chỉ</label>
                       <select required value={phuongThuc} onChange={(e) => setPhuongThuc(e.target.value)} className="w-full px-4 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 font-medium">
                         <option value="IELTS">IELTS</option>
                         <option value="TOEFL">TOEFL iBT</option>
                         <option value="TOEIC">TOEIC</option>
                         <option value="VSTEP">VSTEP</option>
                       </select>
                     </div>
                     <div>
                       <label className="block text-sm font-bold text-slate-700 mb-2">Môn áp dụng</label>
                       <input type="text" readOnly value={mon} className="w-full px-4 py-3 bg-slate-200/50 border border-slate-200 rounded-xl text-slate-500 font-semibold cursor-not-allowed" />
                     </div>
                   </div>
                   <div className="grid grid-cols-2 gap-6">
                     <div>
                       <label className="block text-sm font-bold text-slate-700 mb-2">Mốc điểm gốc (VD: 6.0)</label>
                       <input type="number" step="0.1" required value={diemA} onChange={(e) => setDiemA(e.target.value)} className="w-full px-4 py-3 border border-blue-200 rounded-xl focus:ring-2 focus:ring-blue-500 font-bold text-blue-900 bg-white" placeholder="Mốc chứng chỉ gốc" />
                     </div>
                     <div>
                       <label className="block text-sm font-bold text-slate-700 mb-2">Điểm quy đổi (VD: 8.5)</label>
                       <input type="number" step="0.1" required value={diemB} onChange={(e) => setDiemB(e.target.value)} className="w-full px-4 py-3 border border-emerald-200 rounded-xl focus:ring-2 focus:ring-emerald-500 font-bold text-emerald-700 bg-white" placeholder="Điểm THPT quy đổi" />
                     </div>
                   </div>
                </div>
              ) : (
                <div className="space-y-6 bg-slate-50 p-6 rounded-2xl border border-slate-100">
                   <div className="grid grid-cols-2 md:grid-cols-3 gap-6">
                     <div>
                       <label className="block text-sm font-bold text-slate-700 mb-2">Phương thức</label>
                       <input type="text" readOnly value={phuongThuc} className="w-full px-4 py-3 bg-slate-200/50 border border-slate-200 rounded-xl text-slate-500 font-semibold cursor-not-allowed" />
                     </div>
                     <div>
                       <label className="block text-sm font-bold text-slate-700 mb-2">Môn thi V-SAT</label>
                       <select required value={mon} onChange={(e) => setMon(e.target.value)} className="w-full px-4 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 font-medium">
                         <option value="TO">Toán (TO)</option>
                         <option value="LI">Vật Lý (LI)</option>
                         <option value="HO">Hóa Học (HO)</option>
                         <option value="SI">Sinh Học (SI)</option>
                         <option value="VA">Ngữ Văn (VA)</option>
                         <option value="SU">Lịch Sử (SU)</option>
                         <option value="DI">Địa Lý (DI)</option>
                         <option value="TI">Tiếng Anh (TI)</option>
                       </select>
                     </div>
                     <div className="col-span-2 md:col-span-1">
                       <label className="block text-sm font-bold text-slate-700 mb-2">Nhóm Phân vị (%)</label>
                       <input type="number" step="0.1" required value={phanVi} onChange={(e) => setPhanVi(e.target.value)} placeholder="VD: 20" className="w-full px-4 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 bg-white" />
                     </div>
                   </div>
                   
                   <hr className="border-slate-200" />
                   
                   <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                     <div className="space-y-4">
                       <h4 className="text-sm font-bold text-blue-800 uppercase tracking-wider text-center bg-blue-100/50 py-2 rounded-lg">Trục x: Điểm V-SAT</h4>
                       <div>
                         <label className="block text-xs font-bold text-blue-600 uppercase mb-1">Mốc dưới V-SAT (a)</label>
                         <input type="number" step="0.01" required value={diemA} onChange={(e) => setDiemA(e.target.value)} className="w-full px-4 py-3 border-2 border-blue-200 focus:border-blue-500 rounded-xl text-center font-semibold text-blue-900" />
                       </div>
                       <div>
                         <label className="block text-xs font-bold text-blue-600 uppercase mb-1">Mốc trên V-SAT (b)</label>
                         <input type="number" step="0.01" required value={diemB} onChange={(e) => setDiemB(e.target.value)} className="w-full px-4 py-3 border-2 border-blue-200 focus:border-blue-500 rounded-xl text-center font-semibold text-blue-900" />
                       </div>
                     </div>
                     <div className="space-y-4">
                       <h4 className="text-sm font-bold text-emerald-800 uppercase tracking-wider text-center bg-emerald-100/50 py-2 rounded-lg">Trục y: Điểm THPT</h4>
                       <div>
                         <label className="block text-xs font-bold text-emerald-600 uppercase mb-1">Mốc dưới THPT (c)</label>
                         <input type="number" step="0.01" required value={diemC} onChange={(e) => setDiemC(e.target.value)} className="w-full px-4 py-3 border-2 border-emerald-200 focus:border-emerald-500 rounded-xl text-center font-semibold text-emerald-900" />
                       </div>
                       <div>
                         <label className="block text-xs font-bold text-emerald-600 uppercase mb-1">Mốc trên THPT (d)</label>
                         <input type="number" step="0.01" required value={diemD} onChange={(e) => setDiemD(e.target.value)} className="w-full px-4 py-3 border-2 border-emerald-200 focus:border-emerald-500 rounded-xl text-center font-semibold text-emerald-900" />
                       </div>
                     </div>
                   </div>
                </div>
              )}

              {/* 3. AUTO GENERATED CODE & SUBMIT */}
              <div className="mt-8 flex flex-col sm:flex-row items-end gap-4 pt-6 border-t border-slate-100">
                <div className="flex-1 w-full">
                  <label className="text-xs font-bold text-slate-500 uppercase flex items-center gap-2">
                    <svg className="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
                    Mã quy tắc tự sinh (Hệ thống dùng)
                  </label>
                  <input disabled value={maQuyDoi} className="w-full mt-2 px-4 py-3 bg-slate-800 text-green-400 font-mono rounded-xl border border-slate-700 shadow-inner" />
                </div>
                <button type="submit" className="w-full sm:w-auto bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-xl font-bold shadow-md shadow-blue-500/30 transition-all flex items-center justify-center gap-2">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4"></path></svg>
                  Lưu Quy Tắc
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ImportModal 
        isOpen={isImportOpen} 
        onClose={() => setIsImportOpen(false)} 
        onImport={handleImport} 
        onDownloadTemplate={handleDownloadTemplate}
        title="Import Bảng Nội Suy / Quy Đổi" 
      />
    </div>
  );
}