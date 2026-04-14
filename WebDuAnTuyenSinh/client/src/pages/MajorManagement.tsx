import React, { useState } from 'react';
import { useAppContext, Major } from '../context/AppContext';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
export function MajorManagement() {
  const { majors, setMajors } = useAppContext();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [editingMajor, setEditingMajor] = useState<Major | null>(null);
  const [formData, setFormData] = useState({
    maNganh: '',
    tenNganh: '',
    chiTieu: ''
  });
  const handleAdd = () => {
    setEditingMajor(null);
    setFormData({
      maNganh: '',
      tenNganh: '',
      chiTieu: ''
    });
    setIsModalOpen(true);
  };
  const handleEdit = (major: Major) => {
    setEditingMajor(major);
    setFormData({
      maNganh: major.maNganh,
      tenNganh: major.tenNganh,
      chiTieu: major.chiTieu.toString()
    });
    setIsModalOpen(true);
  };
  const handleDelete = (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa ngành này?')) {
      setMajors(majors.filter((m) => m.id !== id));
    }
  };
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const chiTieu = parseInt(formData.chiTieu);
    if (editingMajor) {
      setMajors(
        majors.map((m) =>
        m.id === editingMajor.id ?
        {
          ...m,
          ...formData,
          chiTieu
        } :
        m
        )
      );
    } else {
      const newMajor: Major = {
        id: Date.now().toString(),
        maNganh: formData.maNganh,
        tenNganh: formData.tenNganh,
        chiTieu
      };
      setMajors([...majors, newMajor]);
    }
    setIsModalOpen(false);
  };
  const handleImport = (data: any[]) => {
    const newMajors = data.map((row, index) => ({
      id: `imported-${Date.now()}-${index}`,
      maNganh: row['Mã ngành'] || '',
      tenNganh: row['Tên ngành'] || '',
      chiTieu: parseInt(row['Chỉ tiêu']) || 0
    }));
    setMajors([...majors, ...newMajors]);
  };
  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Quản lý ngành
        </h1>
        <p className="text-slate-600">
          Danh sách ngành đào tạo và chỉ tiêu tuyển sinh
        </p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-slate-800">
              Danh sách ngành
            </h2>
            <div className="flex gap-3">
              <button
                onClick={() => setIsImportOpen(true)}
                className="flex items-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg font-medium transition-colors">
                
                <UploadIcon size={20} />
                Import
              </button>
              <button
                onClick={handleAdd}
                className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors">
                
                <PlusIcon size={20} />
                Thêm ngành
              </button>
            </div>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Mã ngành
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Tên ngành
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Chỉ tiêu
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {majors.map((major) =>
              <tr
                key={major.id}
                className="hover:bg-slate-50 transition-colors">
                
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">
                    {major.maNganh}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {major.tenNganh}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {major.chiTieu}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                      onClick={() => handleEdit(major)}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded transition-colors">
                      
                        <EditIcon size={18} />
                      </button>
                      <button
                      onClick={() => handleDelete(major.id)}
                      className="p-2 text-red-600 hover:bg-red-50 rounded transition-colors">
                      
                        <TrashIcon size={18} />
                      </button>
                    </div>
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
                {editingMajor ? 'Chỉnh sửa ngành' : 'Thêm ngành mới'}
              </h2>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Mã ngành
                </label>
                <input
                type="text"
                required
                value={formData.maNganh}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  maNganh: e.target.value
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
              
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Tên ngành
                </label>
                <input
                type="text"
                required
                value={formData.tenNganh}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  tenNganh: e.target.value
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
              
              </div>

              <div className="mb-6">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Chỉ tiêu
                </label>
                <input
                type="number"
                min="1"
                required
                value={formData.chiTieu}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  chiTieu: e.target.value
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
              
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

      <ImportModal
        isOpen={isImportOpen}
        onClose={() => setIsImportOpen(false)}
        onImport={handleImport}
        columns={['Mã ngành', 'Tên ngành', 'Chỉ tiêu']}
        title="Import danh sách ngành" />
      
    </div>);

}