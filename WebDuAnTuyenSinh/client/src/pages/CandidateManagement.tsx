import React, { useState, useEffect } from 'react';
import { useAppContext, Candidate } from '../context/AppContext';
import { candidateService } from '../services/candidateService';
import { PlusIcon, EditIcon, TrashIcon, SearchIcon, UploadIcon } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import { Pagination } from '../components/Pagination';

export function CandidateManagement() {
  const { candidates, setCandidates } = useAppContext();
  const [searchTerm, setSearchTerm] = useState('');

  // Fetch data from Node.js backend
  useEffect(() => {
    const fetchCandidates = async () => {
      try {
        const data = await candidateService.getAllCandidates();
        setCandidates(data);
      } catch (error) {
        console.error('Error fetching candidates:', error);
      }
    };
    fetchCandidates();
  }, [setCandidates]);

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 20;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [editingCandidate, setEditingCandidate] = useState<Candidate | null>(null);

  const initialForm = {
    cccd: '',
    soBaoDanh: '',
    ho: '',
    ten: '',
    ngaySinh: '',
    dienThoai: '',
    password: '',
    gioiTinh: 'Nam',
    email: '',
    noiSinh: '',
    doiTuong: '',
    khuVuc: ''
  };

  const [formData, setFormData] = useState(initialForm);

  const filteredCandidates = candidates.filter(
    (c) =>
      (c.ho + ' ' + c.ten).toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.cccd.includes(searchTerm) ||
      (c.soBaoDanh && c.soBaoDanh.includes(searchTerm))
  );

  const paginatedCandidates = filteredCandidates.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handleAdd = () => {
    setEditingCandidate(null);
    setFormData(initialForm);
    setIsModalOpen(true);
  };

  const handleEdit = (candidate: Candidate) => {
    setEditingCandidate(candidate);
    setFormData({
      cccd: candidate.cccd || '',
      soBaoDanh: candidate.soBaoDanh || '',
      ho: candidate.ho || '',
      ten: candidate.ten || '',
      ngaySinh: candidate.ngaySinh || '',
      dienThoai: candidate.dienThoai || '',
      password: '', // Ẩn mật khẩu khi edit
      gioiTinh: candidate.gioiTinh || 'Nam',
      email: candidate.email || '',
      noiSinh: candidate.noiSinh || '',
      doiTuong: candidate.doiTuong || '',
      khuVuc: candidate.khuVuc || ''
    });
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa thí sinh này?')) {
      try {
        await candidateService.delete(id);
        setCandidates(candidates.filter((c) => c.cccd !== id && c.id !== id));
      } catch (error) {
        console.error(error);
        alert('Xoá thất bại!');
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingCandidate) {
        const updated = await candidateService.update(editingCandidate.cccd, formData);
        setCandidates(
          candidates.map((c) =>
            c.id === editingCandidate.id ? { ...c, ...updated } : c
          )
        );
      } else {
        const result = await candidateService.create(formData);
        const newCandidate: Candidate = {
          id: result.cccd,
          ...result
        };
        setCandidates([...candidates, newCandidate]);
      }
      setIsModalOpen(false);
    } catch (error) {
      console.error(error);
      alert('Lưu thất bại!');
    }
  };

  const handleImport = (data: any[]) => {
    // Basic mapping cho import
    const newCandidates = data.map((row, index) => ({
      id: `imported-${Date.now()}-${index}`,
      cccd: row['CCCD'] || '',
      soBaoDanh: row['SBD'] || '',
      ho: row['Họ'] || '',
      ten: row['Tên'] || '',
      ngaySinh: row['Ngày sinh'] || '',
      dienThoai: row['SĐT'] || '',
      password: row['Password'] || '',
      gioiTinh: row['Giới tính'] || 'Nam',
      email: row['Email'] || '',
      noiSinh: row['Nơi sinh'] || '',
      doiTuong: row['Đối tượng'] || '',
      khuVuc: row['Khu vực'] || ''
    }));
    setCandidates([...candidates, ...newCandidates]);
  };

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">Quản lý thí sinh</h1>
        <p className="text-slate-600">Danh sách thông tin cá nhân của thí sinh</p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="w-full sm:w-96 relative">
              <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400" size={20} />
              <input
                type="text"
                placeholder="Tìm kiếm theo tên, CCCD hoặc SBD..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setCurrentPage(1);
                }}
                className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="flex w-full sm:w-auto gap-3">
              <button
                onClick={() => setIsImportOpen(true)}
                className="flex-1 sm:flex-none flex items-center justify-center gap-2 bg-white border border-slate-300 text-slate-700 px-4 py-2 rounded-lg hover:bg-slate-50 transition-colors"
              >
                <UploadIcon size={20} />
                Import
              </button>
              <button
                onClick={handleAdd}
                className="flex-1 sm:flex-none flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors"
              >
                <PlusIcon size={20} />
                Thêm thí sinh
              </button>
            </div>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-max">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">CCCD</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">SBD</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Họ</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Tên</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Ngày sinh</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Điện thoại</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Email</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Giới tính</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Nơi sinh</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Đối tượng</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Khu vực</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedCandidates.map((candidate) => (
                <tr key={candidate.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.cccd}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.soBaoDanh}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.ho}</td>
                  <td className="px-4 py-3 text-sm font-medium text-slate-800">{candidate.ten}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.ngaySinh}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.dienThoai}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.email}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.gioiTinh}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.noiSinh}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.doiTuong}</td>
                  <td className="px-4 py-3 text-sm text-slate-800">{candidate.khuVuc}</td>
                  <td className="px-4 py-3 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleEdit(candidate)}
                        className="p-1 text-blue-600 hover:bg-blue-50 rounded transition-colors"
                      >
                        <EditIcon size={18} />
                      </button>
                      <button
                        onClick={() => handleDelete(candidate.id)}
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

        <Pagination
          currentPage={currentPage}
          totalItems={filteredCandidates.length}
          itemsPerPage={itemsPerPage}
          onPageChange={setCurrentPage}
        />
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-slate-200 sticky top-0 bg-white z-10">
              <h2 className="text-xl font-bold text-slate-800">
                {editingCandidate ? 'Chỉnh sửa thí sinh' : 'Thêm thí sinh mới'}
              </h2>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">CCCD *</label>
                  <input type="text" required value={formData.cccd} onChange={(e) => setFormData({ ...formData, cccd: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" disabled={!!editingCandidate} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Số báo danh</label>
                  <input type="text" value={formData.soBaoDanh} onChange={(e) => setFormData({ ...formData, soBaoDanh: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Mật khẩu</label>
                  <input type="text" placeholder={editingCandidate ? "(Bỏ trống nếu giữ nguyên)" : "P123456"} value={formData.password} onChange={(e) => setFormData({ ...formData, password: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Họ *</label>
                  <input type="text" required value={formData.ho} onChange={(e) => setFormData({ ...formData, ho: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tên *</label>
                  <input type="text" required value={formData.ten} onChange={(e) => setFormData({ ...formData, ten: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Ngày sinh *</label>
                  <input type="date" required value={formData.ngaySinh} onChange={(e) => setFormData({ ...formData, ngaySinh: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Giới tính *</label>
                  <select required value={formData.gioiTinh} onChange={(e) => setFormData({ ...formData, gioiTinh: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Điện thoại</label>
                  <input type="tel" value={formData.dienThoai} onChange={(e) => setFormData({ ...formData, dienThoai: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Email</label>
                  <input type="email" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Nơi sinh</label>
                  <input type="text" value={formData.noiSinh} onChange={(e) => setFormData({ ...formData, noiSinh: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Đối tượng</label>
                  <input type="text" value={formData.doiTuong} onChange={(e) => setFormData({ ...formData, doiTuong: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Khu vực</label>
                  <input type="text" value={formData.khuVuc} onChange={(e) => setFormData({ ...formData, khuVuc: e.target.value })} className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 transition-colors"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
                >
                  Lưu Thông Tin
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
        columns={['CCCD', 'SBD', 'Họ', 'Tên']}
        title="Import danh sách thí sinh"
      />
    </div>
  );
}