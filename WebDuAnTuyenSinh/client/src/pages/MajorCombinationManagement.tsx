import React, { useState } from 'react';
import { useAppContext, MajorCombination } from '../context/AppContext';
import { majorComboService } from '../services/majorComboService';
import { PlusIcon, TrashIcon, Loader2 } from 'lucide-react';
export function MajorCombinationManagement() {
  const {
    majorCombinations,
    setMajorCombinations,
    majors,
    subjectCombinations,
    isLoading
  } = useAppContext();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    maNganh: '',
    maToHop: ''
  });
  const handleAdd = () => {
    setFormData({
      maNganh: '',
      maToHop: ''
    });
    setIsModalOpen(true);
  };
  const handleDelete = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa liên kết này?')) {
      try {
        await majorComboService.delete(id);
        setMajorCombinations(majorCombinations.filter((c) => c.id !== id));
      } catch (err) {
        alert("Lỗi khi xóa");
      }
    }
  };
  // Bảng chênh lệch (Hàng: Tổ hợp gốc, Cột: Tổ hợp hiện tại)
  const DEVIATION_TABLE: Record<string, Record<string, number>> = {
    'A00': { 'A00': 0, 'A01': -0.69, 'B00': -1.21, 'C00': 2.32, 'C01': 0.94, 'D01': -0.68, 'D07': -1.62 },
    'A01': { 'A00': 0.69, 'A01': 0, 'B00': -0.52, 'C00': 3.01, 'C01': 1.63, 'D01': 0.01, 'D07': -0.93 },
    'B00': { 'A00': 1.21, 'A01': 0.52, 'B00': 0, 'C00': 3.53, 'C01': 2.15, 'D01': 0.53, 'D07': -0.41 },
    'C00': { 'A00': -2.32, 'A01': -3.01, 'B00': -3.53, 'C00': 0, 'C01': -1.38, 'D01': -3.00, 'D07': -3.94 },
    'C01': { 'A00': -0.94, 'A01': -1.63, 'B00': -2.15, 'C00': 1.38, 'C01': 0, 'D01': -1.62, 'D07': -2.56 },
    'D01': { 'A00': 0.68, 'A01': -0.01, 'B00': -0.53, 'C00': 3.00, 'C01': 1.62, 'D01': 0, 'D07': -0.94 }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // Check if mapping already exists
    const exists = majorCombinations.some(
      (c) => c.maNganh === formData.maNganh && c.maToHop === formData.maToHop
    );
    if (exists) {
      alert('Liên kết này đã tồn tại!');
      return;
    }
    
    // Tính tự động độ lệch
    const major = majors.find(m => m.maNganh === formData.maNganh);
    const toHopGoc = major?.toHopGoc || 'A00';
    let doLech = 0;
    if (DEVIATION_TABLE[toHopGoc] && DEVIATION_TABLE[toHopGoc][formData.maToHop] !== undefined) {
      doLech = DEVIATION_TABLE[toHopGoc][formData.maToHop];
    }
    
    const payload = { ...formData, doLech };

    try {
      const result = await majorComboService.create(payload);
      const newCombo: MajorCombination = {
        id: result.id,
        maNganh: formData.maNganh,
        maToHop: formData.maToHop,
        doLech: doLech
      };
      setMajorCombinations([...majorCombinations, newCombo]);
      setIsModalOpen(false);
    } catch (err) {
      alert("Lỗi lưu liên kết");
    }
  };
  // Helper to get names
  const getMajorName = (maNganh: string) =>
    majors.find((m) => m.maNganh === maNganh)?.tenNganh || maNganh;
  const getComboName = (maToHop: string) =>
    subjectCombinations.find((c) => c.maToHop === maToHop)?.tenToHop || maToHop;

  const subjectNames: Record<string, string> = {
    'TO': 'Toán', 'LI': 'Lý', 'HO': 'Hóa', 'SU': 'Sử', 'DI': 'Địa',
    'VA': 'Văn', 'SI': 'Sinh', 'N1': 'Ngoại ngữ', 'TI': 'Tiếng Anh',
    'KHAC': 'Môn khác', 'KTPL': 'KTPL'
  };

  const getSubjectDetail = (combo: MajorCombination) => {
    if (!combo.thMon1) return <span className="text-slate-400 italic">Chưa map môn</span>;
    const mapName = (code?: string) => code ? (subjectNames[code] || code) : '';
    
    const s1 = combo.thMon1 ? `${mapName(combo.thMon1)} x${combo.hsMon1 || 1}` : '';
    const s2 = combo.thMon2 ? ` - ${mapName(combo.thMon2)} x${combo.hsMon2 || 1}` : '';
    const s3 = combo.thMon3 ? ` - ${mapName(combo.thMon3)} x${combo.hsMon3 || 1}` : '';
    
    return s1 + s2 + s3;
  };

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Quản lý ngành - Tổ hợp
        </h1>
        <p className="text-slate-600">
          Cấu hình các tổ hợp môn xét tuyển cho từng ngành
        </p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-slate-800">
              Danh sách liên kết
            </h2>
            <button
              onClick={handleAdd}
              className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors">
              
              <PlusIcon size={20} />
              Thêm liên kết
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
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Ngành
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Tổ hợp
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Chi tiết Môn & Hệ số
                </th>
                <th className="px-6 py-3 text-center text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Độ lệch
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {majorCombinations.map((combo) =>
              <tr
                key={combo.id}
                className="hover:bg-slate-50 transition-colors">
                
                  <td className="px-6 py-4">
                    <div className="text-sm font-medium text-slate-800">
                      {combo.maNganh}
                    </div>
                    <div className="text-xs text-slate-500">
                      {getMajorName(combo.maNganh)}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-sm font-medium text-slate-800">
                      {combo.maToHop}
                    </div>
                    <div className="text-xs text-slate-500">
                      {getComboName(combo.maToHop)}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-sm font-medium text-blue-700 bg-blue-50 px-3 py-1 rounded inline-block">
                      {getSubjectDetail(combo)}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-center">
                    <div className="text-sm font-bold text-slate-700">
                      {combo.doLech !== undefined ? parseFloat(combo.doLech.toString()).toFixed(2) : '0.00'}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <button
                    onClick={() => handleDelete(combo.id)}
                    className="p-2 text-red-600 hover:bg-red-50 rounded transition-colors"
                    title="Xóa liên kết">
                    
                      <TrashIcon size={18} />
                    </button>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {isModalOpen &&
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
            <div className="p-6 border-b border-slate-200">
              <h2 className="text-xl font-bold text-slate-800">
                Thêm liên kết Ngành - Tổ hợp
              </h2>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Chọn ngành
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
                
                  <option value="">-- Chọn ngành --</option>
                  {majors.map((m) =>
                <option key={m.id} value={m.maNganh}>
                      {m.maNganh} - {m.tenNganh}
                    </option>
                )}
                </select>
              </div>

              <div className="mb-6">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Chọn tổ hợp
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
                
                  <option value="">-- Chọn tổ hợp --</option>
                  {subjectCombinations.map((c) =>
                <option key={c.id} value={c.maToHop}>
                      {c.maToHop} - {c.tenToHop}
                    </option>
                )}
                </select>
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