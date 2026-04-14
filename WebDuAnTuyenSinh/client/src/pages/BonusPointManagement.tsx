import React, { useState } from 'react';
import { useAppContext, BonusPoint } from '../context/AppContext';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
export function BonusPointManagement() {
  const { bonusPoints, setBonusPoints } = useAppContext();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [editingPoint, setEditingPoint] = useState<BonusPoint | null>(null);
  const [formData, setFormData] = useState({
    cccd: '',
    hoTen: '',
    loaiDiemCong: '',
    diem: '',
    ghiChu: ''
  });
  const handleAdd = () => {
    setEditingPoint(null);
    setFormData({
      cccd: '',
      hoTen: '',
      loaiDiemCong: '',
      diem: '',
      ghiChu: ''
    });
    setIsModalOpen(true);
  };
  const handleEdit = (point: BonusPoint) => {
    setEditingPoint(point);
    setFormData({
      cccd: point.cccd,
      hoTen: point.hoTen,
      loaiDiemCong: point.loaiDiemCong,
      diem: point.diem.toString(),
      ghiChu: point.ghiChu
    });
    setIsModalOpen(true);
  };
  const handleDelete = (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa điểm cộng này?')) {
      setBonusPoints(bonusPoints.filter((p) => p.id !== id));
    }
  };
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const diemNum = parseFloat(formData.diem);
    if (editingPoint) {
      setBonusPoints(
        bonusPoints.map((p) =>
        p.id === editingPoint.id ?
        {
          ...p,
          ...formData,
          diem: diemNum
        } :
        p
        )
      );
    } else {
      const newPoint: BonusPoint = {
        id: Date.now().toString(),
        cccd: formData.cccd,
        hoTen: formData.hoTen,
        loaiDiemCong: formData.loaiDiemCong,
        diem: diemNum,
        ghiChu: formData.ghiChu
      };
      setBonusPoints([...bonusPoints, newPoint]);
    }
    setIsModalOpen(false);
  };
  const handleImport = (data: any[]) => {
    const newPoints = data.map((row, index) => ({
      id: `imported-${Date.now()}-${index}`,
      cccd: row['CCCD'] || '',
      hoTen: row['Họ tên'] || '',
      loaiDiemCong: row['Loại điểm cộng'] || '',
      diem: parseFloat(row['Điểm']) || 0,
      ghiChu: row['Ghi chú'] || ''
    }));
    setBonusPoints([...bonusPoints, ...newPoints]);
  };
  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Quản lý điểm cộng
        </h1>
        <p className="text-slate-600">
          Quản lý điểm ưu tiên khu vực, đối tượng
        </p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-slate-800">
              Danh sách điểm cộng
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
                Thêm điểm cộng
              </button>
            </div>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  CCCD
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Họ tên
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Loại điểm cộng
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Điểm
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Ghi chú
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {bonusPoints.map((point) =>
              <tr
                key={point.id}
                className="hover:bg-slate-50 transition-colors">
                
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {point.cccd}
                  </td>
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">
                    {point.hoTen}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                      {point.loaiDiemCong}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm font-semibold text-green-600">
                    +{point.diem}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500">
                    {point.ghiChu}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                      onClick={() => handleEdit(point)}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded transition-colors">
                      
                        <EditIcon size={18} />
                      </button>
                      <button
                      onClick={() => handleDelete(point.id)}
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
                {editingPoint ? 'Chỉnh sửa điểm cộng' : 'Thêm điểm cộng'}
              </h2>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
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

              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Loại điểm cộng
                  </label>
                  <input
                  type="text"
                  required
                  value={formData.loaiDiemCong}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    loaiDiemCong: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="VD: KV1, ĐT1" />
                
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Mức điểm
                  </label>
                  <input
                  type="number"
                  step="0.25"
                  required
                  value={formData.diem}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    diem: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                
                </div>
              </div>

              <div className="mb-6">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Ghi chú
                </label>
                <input
                type="text"
                value={formData.ghiChu}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  ghiChu: e.target.value
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="VD: Khu vực 1" />
              
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
        columns={['CCCD', 'Họ tên', 'Loại điểm cộng', 'Điểm', 'Ghi chú']}
        title="Import điểm cộng ưu tiên" />
      
    </div>);

}