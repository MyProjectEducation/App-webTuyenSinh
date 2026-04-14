import React, { useState } from 'react';
import { useAppContext, MajorCombination } from '../context/AppContext';
import { PlusIcon, TrashIcon } from 'lucide-react';
export function MajorCombinationManagement() {
  const {
    majorCombinations,
    setMajorCombinations,
    majors,
    subjectCombinations
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
  const handleDelete = (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa liên kết này?')) {
      setMajorCombinations(majorCombinations.filter((c) => c.id !== id));
    }
  };
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Check if mapping already exists
    const exists = majorCombinations.some(
      (c) => c.maNganh === formData.maNganh && c.maToHop === formData.maToHop
    );
    if (exists) {
      alert('Liên kết này đã tồn tại!');
      return;
    }
    const newCombo: MajorCombination = {
      id: Date.now().toString(),
      maNganh: formData.maNganh,
      maToHop: formData.maToHop
    };
    setMajorCombinations([...majorCombinations, newCombo]);
    setIsModalOpen(false);
  };
  // Helper to get names
  const getMajorName = (maNganh: string) =>
  majors.find((m) => m.maNganh === maNganh)?.tenNganh || maNganh;
  const getComboName = (maToHop: string) =>
  subjectCombinations.find((c) => c.maToHop === maToHop)?.tenToHop || maToHop;
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

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Ngành
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Tổ hợp môn
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