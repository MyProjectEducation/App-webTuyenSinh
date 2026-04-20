import React, { useState, useMemo, useEffect } from 'react';
import { useAppContext, ConversionRule } from '../context/AppContext';
import { conversionService } from '../services/conversionService';
import { EditIcon, TrashIcon, PlusIcon, UploadIcon, SearchIcon, Loader2 } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import { Pagination } from '../components/Pagination';

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

  const handleImport = (data: any[]) => {
    // Để trống cho gọn phần demo
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

        <Pagination currentPage={currentPage} totalItems={filteredRules.length} itemsPerPage={itemsPerPage} onPageChange={setCurrentPage} />
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-2xl w-full max-w-3xl overflow-hidden flex flex-col max-h-[90vh]">
            <div className="p-5 border-b border-slate-200 shrink-0 bg-slate-50 flex justify-between items-center">
              <div>
                <h2 className="text-xl font-bold text-slate-800">
                  {editingRule ? 'Biên dịch Quy tắc Toán học' : 'Khai báo Thuật toán Quy đổi'}
                </h2>
                <p className="text-sm text-slate-500 mt-1">Hệ thống Auto-Generative ID đảm bảo không trùng lặp Khóa Chính.</p>
              </div>
              <div className="text-right">
                <div className="text-xs text-slate-500 font-semibold mb-1 uppercase tracking-wide">Mã định danh (Auto)</div>
                <div className="px-3 py-1 bg-slate-800 text-green-400 rounded-md font-mono text-sm tracking-wider shadow-inner">
                  {maQuyDoi}
                </div>
              </div>
            </div>

            <form onSubmit={handleSubmit} className="p-6 overflow-y-auto">
              
              <div className="mb-6 flex gap-4">
                <label className="flex items-center gap-2 px-4 py-3 border rounded-lg cursor-pointer flex-1 transition-colors hover:bg-slate-50">
                  <input type="radio" name="formType" value="NGOAI_NGU" checked={formType === 'NGOAI_NGU'} onChange={() => setFormType('NGOAI_NGU')} className="text-blue-600 w-4 h-4" />
                  <span className="font-semibold text-slate-700">Quy đổi Ngoại ngữ</span>
                </label>
                <label className="flex items-center gap-2 px-4 py-3 border rounded-lg cursor-pointer flex-1 transition-colors hover:bg-slate-50">
                  <input type="radio" name="formType" value="VSAT" checked={formType === 'VSAT'} onChange={() => setFormType('VSAT')} className="text-blue-600 w-4 h-4" />
                  <span className="font-semibold text-slate-700">Nội suy V-SAT (Tuyến tính)</span>
                </label>
              </div>

              {formType === 'NGOAI_NGU' ? (
                <div className="space-y-5 bg-blue-50 p-5 rounded border border-blue-100">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Loại Chứng Chỉ</label>
                      <select value={phuongThuc} onChange={(e) => setPhuongThuc(e.target.value)} className="w-full px-3 py-2 border border-slate-300 rounded focus:ring-blue-500">
                        <option value="IELTS">IELTS</option>
                        <option value="TOEFL">TOEFL iBT</option>
                        <option value="TOEIC">TOEIC</option>
                        <option value="VSTEP">VSTEP</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Môn áp dụng</label>
                      <input type="text" readOnly value={mon} className="w-full px-3 py-2 bg-slate-100 border border-slate-300 rounded text-slate-500 font-semibold cursor-not-allowed" />
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Mức điểm gốc (VD: 5.5)</label>
                      <input type="number" step="0.1" required value={diemA} onChange={(e) => setDiemA(e.target.value)} className="w-full px-3 py-2 border border-blue-300 shadow-sm rounded focus:ring-blue-500 font-bold text-blue-900" />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Điểm quy đổi thang 10 (VD: 8.0)</label>
                      <input type="number" step="0.1" required value={diemB} onChange={(e) => setDiemB(e.target.value)} className="w-full px-3 py-2 border border-emerald-300 shadow-sm rounded focus:ring-emerald-500 font-bold text-emerald-700" />
                    </div>
                  </div>
                </div>
              ) : (
                <div className="space-y-6">
                  <div className="grid grid-cols-3 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Phương thức</label>
                      <input type="text" readOnly value={phuongThuc} className="w-full px-3 py-2 bg-slate-100 border border-slate-300 rounded text-slate-500 font-semibold cursor-not-allowed" />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Môn Xét Tuyển</label>
                      <select required value={mon} onChange={(e) => setMon(e.target.value)} className="w-full px-3 py-2 border border-slate-300 rounded focus:ring-blue-500">
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
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-1">Cấp Phân Vị (%)</label>
                      <input type="number" step="0.1" required placeholder="VD: 20" value={phanVi} onChange={(e) => setPhanVi(e.target.value)} className="w-full px-3 py-2 border border-slate-300 rounded focus:ring-blue-500" />
                    </div>
                  </div>

                  <div className="p-5 bg-slate-50 border border-slate-200 rounded-lg">
                    <p className="text-sm font-semibold text-slate-800 mb-3 uppercase tracking-wider text-center">BIẾN THEO HỆ TRỤC NỘI SUY y = c + [(x - a) / (b - a)] * (d - c)</p>
                    
                    <div className="grid grid-cols-2 gap-8 relative">
                      <div className="absolute left-1/2 top-0 bottom-0 w-px bg-slate-300 -translate-x-1/2 hidden md:block"></div>
                      
                      <div className="space-y-4">
                        <h4 className="text-center text-blue-800 font-bold border-b pb-2">Trục x: Khoảng điểm V-SAT</h4>
                        <div>
                          <label className="block text-xs font-semibold text-blue-600 uppercase mb-1">Mốc A (Cận Dưới)</label>
                          <input type="number" step="0.01" required value={diemA} onChange={(e) => setDiemA(e.target.value)} className="w-full px-3 py-2 border-2 border-blue-200 rounded bg-white text-center focus:border-blue-500" />
                        </div>
                        <div>
                          <label className="block text-xs font-semibold text-blue-600 uppercase mb-1">Mốc B (Cận Trên)</label>
                          <input type="number" step="0.01" required value={diemB} onChange={(e) => setDiemB(e.target.value)} className="w-full px-3 py-2 border-2 border-blue-200 rounded bg-white text-center focus:border-blue-500" />
                        </div>
                      </div>

                      <div className="space-y-4">
                        <h4 className="text-center text-emerald-700 font-bold border-b pb-2">Trục y: Tương ứng THPT</h4>
                        <div>
                          <label className="block text-xs font-semibold text-emerald-600 uppercase mb-1">Mốc C (Cận Dưới)</label>
                          <input type="number" step="0.01" required value={diemC} onChange={(e) => setDiemC(e.target.value)} className="w-full px-3 py-2 border-2 border-emerald-200 rounded bg-white text-center focus:border-emerald-500" />
                        </div>
                        <div>
                          <label className="block text-xs font-semibold text-emerald-600 uppercase mb-1">Mốc D (Cận Trên)</label>
                          <input type="number" step="0.01" required value={diemD} onChange={(e) => setDiemD(e.target.value)} className="w-full px-3 py-2 border-2 border-emerald-200 rounded bg-white text-center focus:border-emerald-500" />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              )}

              <div className="flex justify-end gap-3 mt-8 pt-4 border-t border-slate-200">
                <button type="button" onClick={() => setIsModalOpen(false)} className="px-5 py-2.5 text-slate-600 font-medium hover:bg-slate-100 rounded-lg transition-colors">
                  Đóng Form
                </button>
                <button type="submit" className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium shadow-md rounded-lg transition-colors">
                  Ghi Lệnh Chuyển Đổi Vị Phân
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ImportModal isOpen={isImportOpen} onClose={() => setIsImportOpen(false)} onImport={handleImport} columns={['Mã quy đổi', 'Phương thức', 'Môn', 'Điểm a', 'Điểm b', 'Điểm c', 'Điểm d', 'Phân vị']} title="Import Bảng Nội Suy / Quy Đổi" />
    </div>
  );
}