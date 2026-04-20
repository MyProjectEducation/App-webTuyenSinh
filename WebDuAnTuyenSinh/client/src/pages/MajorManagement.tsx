import React, { useState } from 'react';
import { useAppContext, Major } from '../context/AppContext';
import { majorService } from '../services/majorService';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon, Loader2 } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';

export function MajorManagement() {
  const { majors, setMajors, isLoading } = useAppContext();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [editingMajor, setEditingMajor] = useState<Major | null>(null);

  const initialForm = {
    maNganh: '',
    tenNganh: '',
    toHopGoc: '',
    chiTieu: '',
    diemSan: '',
    diemTrungTuyen: '',
    tuyenThang: '0',
    dgnl: '0',
    thpt: '0',
    vsat: '0',
    slXtt: '0',
    slDgnl: '0',
    slThpt: '0',
    slVsat: '0'
  };

  const [formData, setFormData] = useState(initialForm);

  const handleAdd = () => {
    setEditingMajor(null);
    setFormData(initialForm);
    setIsModalOpen(true);
  };

  const handleEdit = (major: Major) => {
    setEditingMajor(major);
    setFormData({
      maNganh: major.maNganh || '',
      tenNganh: major.tenNganh || '',
      toHopGoc: major.toHopGoc || '',
      chiTieu: major.chiTieu?.toString() || '0',
      diemSan: major.diemSan?.toString() || '',
      diemTrungTuyen: major.diemTrungTuyen?.toString() || '',
      tuyenThang: major.tuyenThang || '0',
      dgnl: major.dgnl || '0',
      thpt: major.thpt || '0',
      vsat: major.vsat || '0',
      slXtt: major.slXtt?.toString() || '0',
      slDgnl: major.slDgnl?.toString() || '0',
      slThpt: major.slThpt?.toString() || '0',
      slVsat: major.slVsat?.toString() || '0'
    });
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa ngành này?')) {
      try {
        await majorService.delete(id);
        setMajors(majors.filter((m) => m.id !== id));
      } catch (err) {
        alert("Lỗi khi xoá dữ liệu");
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const payload = {
      maNganh: formData.maNganh,
      tenNganh: formData.tenNganh,
      toHopGoc: formData.toHopGoc,
      chiTieu: parseInt(formData.chiTieu) || 0,
      diemSan: formData.diemSan ? parseFloat(formData.diemSan) : null,
      diemTrungTuyen: formData.diemTrungTuyen ? parseFloat(formData.diemTrungTuyen) : null,
      tuyenThang: formData.tuyenThang,
      dgnl: formData.dgnl,
      thpt: formData.thpt,
      vsat: formData.vsat,
      slXtt: parseInt(formData.slXtt) || 0,
      slDgnl: parseInt(formData.slDgnl) || 0,
      slThpt: parseInt(formData.slThpt) || 0,
      slVsat: parseInt(formData.slVsat) || 0
    };

    try {
      if (editingMajor) {
        await majorService.update(editingMajor.id, payload);
        // Để UI mượt, ta lấy full thuộc tính. Cần refresh toàn bộ hoặc tự map payload -> State.
        // Controller trả về payload. Phải map đúng các thuộc tính của AppContext vào Major
        const updatedMajor: Major = {
            id: editingMajor.id,
            ...payload
        };
        setMajors(majors.map((m) => m.id === editingMajor.id ? updatedMajor : m));
      } else {
        const result = await majorService.create(payload);
        const newMajor: Major = {
          id: result.id,
          ...payload
        };
        setMajors([...majors, newMajor]);
      }
      setIsModalOpen(false);
    } catch (err) {
      alert("Lỗi lưu dữ liệu");
    }
  };

  const handleImport = (data: any[]) => {
    const newMajors = data.map((row, index) => ({
      id: `imported-${Date.now()}-${index}`,
      maNganh: row['Mã ngành'] || '',
      tenNganh: row['Tên ngành'] || '',
      chiTieu: parseInt(row['Chỉ tiêu']) || 0,
      toHopGoc: '',
      diemSan: 0,
      diemTrungTuyen: 0,
      tuyenThang: '0', dgnl: '0', thpt: '0', vsat: '0',
      slXtt: 0, slDgnl: 0, slThpt: 0, slVsat: 0
    }));
    setMajors([...majors, ...newMajors]);
  };

  const formatYesNo = (val: string) => val === '1' ? 'Có' : 'Không';

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">Quản lý ngành</h1>
        <p className="text-slate-600">Danh sách ngành đào tạo và cấu hình phương thức xét tuyển</p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-slate-800">Danh sách ngành</h2>
            <div className="flex gap-3">
              <button
                onClick={() => setIsImportOpen(true)}
                className="flex items-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg font-medium transition-colors"
              >
                <UploadIcon size={20} />
                Import
              </button>
              <button
                onClick={handleAdd}
                className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
              >
                <PlusIcon size={20} />
                Thêm ngành
              </button>
            </div>
          </div>
        </div>

        <div className="overflow-x-auto relative min-h-[200px]">
          {isLoading ? (
            <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
              <Loader2 className="animate-spin text-blue-600" size={32} />
              <span className="ml-2 text-slate-600">Đang tải dữ liệu...</span>
            </div>
          ) : null}
          <table className="w-full min-w-max">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Mã ngành</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Tên ngành</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Tổ hợp môn</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Chỉ tiêu</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Điểm sàn</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Điểm trúng tuyển</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Tuyển thẳng</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">ĐGNL</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">THPT</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">VSAT</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">SL Tuyển Thẳng</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">SL ĐGNL</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">SL THPT</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">SL VSAT</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {majors.map((major) => (
                <tr key={major.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-4 py-3 text-sm font-medium text-slate-800">{major.maNganh}</td>
                  <td className="px-4 py-3 text-sm text-slate-800 max-w-[200px] truncate" title={major.tenNganh}>{major.tenNganh}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{major.toHopGoc}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{major.chiTieu}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{major.diemSan}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{major.diemTrungTuyen}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">
                    <span className={`px-2 py-1 text-xs rounded-full ${major.tuyenThang === '1' ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-600'}`}>{formatYesNo(major.tuyenThang)}</span>
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-800">
                    <span className={`px-2 py-1 text-xs rounded-full ${major.dgnl === '1' ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-600'}`}>{formatYesNo(major.dgnl)}</span>
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-800">
                    <span className={`px-2 py-1 text-xs rounded-full ${major.thpt === '1' ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-600'}`}>{formatYesNo(major.thpt)}</span>
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-800">
                    <span className={`px-2 py-1 text-xs rounded-full ${major.vsat === '1' ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-600'}`}>{formatYesNo(major.vsat)}</span>
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-800">{major.slXtt}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{major.slDgnl}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{major.slThpt}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{major.slVsat}</td>
                  <td className="px-4 py-3 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleEdit(major)}
                        className="p-1 text-blue-600 hover:bg-blue-50 rounded transition-colors"
                      >
                        <EditIcon size={18} />
                      </button>
                      <button
                        onClick={() => handleDelete(major.id)}
                        className="p-1 text-red-600 hover:bg-red-50 rounded transition-colors"
                      >
                        <TrashIcon size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-5xl max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-slate-200 sticky top-0 bg-white z-10 flex justify-between items-center">
              <h2 className="text-xl font-bold text-slate-800">
                {editingMajor ? 'Chỉnh sửa ngành đào tạo' : 'Thêm ngành đào tạo mới'}
              </h2>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
              {/* Thông tin cơ bản */}
              <h3 className="text-md font-semibold text-slate-700 mb-4 pb-2 border-b">Thông tin cơ bản & Tuyển sinh</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 mb-8">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Mã ngành *</label>
                  <input type="text" required value={formData.maNganh} onChange={(e) => setFormData({ ...formData, maNganh: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" disabled={!!editingMajor} />
                </div>
                <div className="md:col-span-2 xl:col-span-1">
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tên ngành *</label>
                  <input type="text" required value={formData.tenNganh} onChange={(e) => setFormData({ ...formData, tenNganh: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tổ hợp môn</label>
                  <input type="text" value={formData.toHopGoc} onChange={(e) => setFormData({ ...formData, toHopGoc: e.target.value })} placeholder="VD: A00,A01" className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tổng Chỉ tiêu *</label>
                  <input type="number" required min="0" value={formData.chiTieu} onChange={(e) => setFormData({ ...formData, chiTieu: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Điểm sàn</label>
                  <input type="number" step="0.01" value={formData.diemSan} onChange={(e) => setFormData({ ...formData, diemSan: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Điểm trúng tuyển</label>
                  <input type="number" step="0.01" value={formData.diemTrungTuyen} onChange={(e) => setFormData({ ...formData, diemTrungTuyen: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>

              {/* Thông tin phương thức xét tuyển */}
              <h3 className="text-md font-semibold text-slate-700 mb-4 pb-2 border-b">Cấu hình Phương thức xét & Phân bổ chỉ tiêu</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-x-6 gap-y-4">
                
                {/* Tuyển Thẳng */}
                <div className="bg-slate-50 p-4 rounded-lg border border-slate-200">
                   <div className="mb-3">
                     <label className="block text-sm font-bold text-slate-700 mb-1">Xét Tuyển Thẳng</label>
                     <select value={formData.tuyenThang} onChange={(e) => setFormData({ ...formData, tuyenThang: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded hover:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option value="0">Không áp dụng</option>
                        <option value="1">Có áp dụng</option>
                     </select>
                   </div>
                   <div>
                     <label className="block text-sm font-medium text-slate-600 mb-1">Chỉ tiêu Tuyển thẳng</label>
                     <input type="number" min="0" value={formData.slXtt} onChange={(e) => setFormData({ ...formData, slXtt: e.target.value })} className={`w-full px-3 py-2 border border-slate-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 ${formData.tuyenThang === '0' ? 'bg-slate-100 text-slate-400' : ''}`} disabled={formData.tuyenThang === '0'} />
                   </div>
                </div>

                {/* ĐGNL */}
                <div className="bg-slate-50 p-4 rounded-lg border border-slate-200">
                   <div className="mb-3">
                     <label className="block text-sm font-bold text-slate-700 mb-1">Xét điểm ĐGNL</label>
                     <select value={formData.dgnl} onChange={(e) => setFormData({ ...formData, dgnl: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded hover:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option value="0">Không áp dụng</option>
                        <option value="1">Có áp dụng</option>
                     </select>
                   </div>
                   <div>
                     <label className="block text-sm font-medium text-slate-600 mb-1">Chỉ tiêu ĐGNL</label>
                     <input type="number" min="0" value={formData.slDgnl} onChange={(e) => setFormData({ ...formData, slDgnl: e.target.value })} className={`w-full px-3 py-2 border border-slate-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 ${formData.dgnl === '0' ? 'bg-slate-100 text-slate-400' : ''}`} disabled={formData.dgnl === '0'} />
                   </div>
                </div>

                {/* THPT */}
                <div className="bg-slate-50 p-4 rounded-lg border border-slate-200">
                   <div className="mb-3">
                     <label className="block text-sm font-bold text-slate-700 mb-1">Xét Kết Quả THPT</label>
                     <select value={formData.thpt} onChange={(e) => setFormData({ ...formData, thpt: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded hover:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option value="0">Không áp dụng</option>
                        <option value="1">Có áp dụng</option>
                     </select>
                   </div>
                   <div>
                     <label className="block text-sm font-medium text-slate-600 mb-1">Chỉ tiêu THPT</label>
                     <input type="number" min="0" value={formData.slThpt} onChange={(e) => setFormData({ ...formData, slThpt: e.target.value })} className={`w-full px-3 py-2 border border-slate-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 ${formData.thpt === '0' ? 'bg-slate-100 text-slate-400' : ''}`} disabled={formData.thpt === '0'} />
                   </div>
                </div>

                {/* VSAT */}
                <div className="bg-slate-50 p-4 rounded-lg border border-slate-200">
                   <div className="mb-3">
                     <label className="block text-sm font-bold text-slate-700 mb-1">Xét điểm VSAT</label>
                     <select value={formData.vsat} onChange={(e) => setFormData({ ...formData, vsat: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded hover:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option value="0">Không áp dụng</option>
                        <option value="1">Có áp dụng</option>
                     </select>
                   </div>
                   <div>
                     <label className="block text-sm font-medium text-slate-600 mb-1">Chỉ tiêu VSAT</label>
                     <input type="number" min="0" value={formData.slVsat} onChange={(e) => setFormData({ ...formData, slVsat: e.target.value })} className={`w-full px-3 py-2 border border-slate-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 ${formData.vsat === '0' ? 'bg-slate-100 text-slate-400' : ''}`} disabled={formData.vsat === '0'} />
                   </div>
                </div>

              </div>

              <div className="flex justify-end gap-3 mt-8 pt-4 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 transition-colors"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors font-medium shadow-sm"
                >
                  Lưu Thông Tin Ngành
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
        columns={['Mã ngành', 'Tên ngành', 'Chỉ tiêu']}
        title="Import danh sách ngành"
      />
    </div>
  );
}