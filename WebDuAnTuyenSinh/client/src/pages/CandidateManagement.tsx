import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAppContext, Candidate } from '../context/AppContext';
import {
  PlusIcon,
  EditIcon,
  TrashIcon,
  SearchIcon,
  UploadIcon } from
'lucide-react';
import { ImportModal } from '../components/ImportModal';
import { Pagination } from '../components/Pagination';
export function CandidateManagement() {
  const { candidates, setCandidates } = useAppContext();
  const [searchTerm, setSearchTerm] = useState('');

  // Fetch data from Node.js backend
  useEffect(() => {
    const fetchCandidates = async () => {
      try {
        const response = await axios.get('http://localhost:5000/api/candidates');
        // Map database fields to frontend Candidate model
        const mappedData = response.data.map((item: any) => ({
          id: item.idthisinh?.toString() || item.cccd,
          cccd: item.cccd || '',
          hoTen: (item.ho || '') + ' ' + (item.ten || ''),
          ngaySinh: item.ngay_sinh || '',
          gioiTinh: item.gioi_tinh || 'Nam',
          diaChi: item.noi_sinh || '',
          sdt: item.dien_thoai || ''
        }));
        setCandidates(mappedData);
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
  const [editingCandidate, setEditingCandidate] = useState<Candidate | null>(
    null
  );
  const [formData, setFormData] = useState({
    cccd: '',
    hoTen: '',
    ngaySinh: '',
    gioiTinh: 'Nam',
    diaChi: '',
    sdt: ''
  });
  const filteredCandidates = candidates.filter(
    (c) =>
    c.hoTen.toLowerCase().includes(searchTerm.toLowerCase()) ||
    c.cccd.includes(searchTerm)
  );
  const paginatedCandidates = filteredCandidates.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );
  const handleAdd = () => {
    setEditingCandidate(null);
    setFormData({
      cccd: '',
      hoTen: '',
      ngaySinh: '',
      gioiTinh: 'Nam',
      diaChi: '',
      sdt: ''
    });
    setIsModalOpen(true);
  };
  const handleEdit = (candidate: Candidate) => {
    setEditingCandidate(candidate);
    setFormData({
      cccd: candidate.cccd,
      hoTen: candidate.hoTen,
      ngaySinh: candidate.ngaySinh,
      gioiTinh: candidate.gioiTinh,
      diaChi: candidate.diaChi,
      sdt: candidate.sdt
    });
    setIsModalOpen(true);
  };
  const handleDelete = (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa thí sinh này?')) {
      setCandidates(candidates.filter((c) => c.id !== id));
    }
  };
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (editingCandidate) {
      setCandidates(
        candidates.map((c) =>
        c.id === editingCandidate.id ?
        {
          ...c,
          ...formData
        } :
        c
        )
      );
    } else {
      const newCandidate: Candidate = {
        id: Date.now().toString(),
        ...formData
      };
      setCandidates([...candidates, newCandidate]);
    }
    setIsModalOpen(false);
  };
  const handleImport = (data: any[]) => {
    const newCandidates = data.map((row, index) => ({
      id: `imported-${Date.now()}-${index}`,
      cccd: row['CCCD'] || '',
      hoTen: row['Họ tên'] || '',
      ngaySinh: row['Ngày sinh'] || '',
      gioiTinh: row['Giới tính'] || 'Nam',
      diaChi: row['Địa chỉ'] || '',
      sdt: row['SĐT'] || ''
    }));
    setCandidates([...candidates, ...newCandidates]);
  };
  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Quản lý thí sinh
        </h1>
        <p className="text-slate-600">
          Danh sách thông tin cá nhân của thí sinh
        </p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="w-full sm:w-96 relative">
              <SearchIcon
                className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"
                size={20} />
              
              <input
                type="text"
                placeholder="Tìm kiếm theo tên hoặc CCCD..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setCurrentPage(1);
                }}
                className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
              
            </div>
            <div className="flex gap-3 w-full sm:w-auto">
              <button
                onClick={() => setIsImportOpen(true)}
                className="flex-1 sm:flex-none flex items-center justify-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg font-medium transition-colors">
                
                <UploadIcon size={20} />
                Import
              </button>
              <button
                onClick={handleAdd}
                className="flex-1 sm:flex-none flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors">
                
                <PlusIcon size={20} />
                Thêm thí sinh
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
                  Ngày sinh
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Giới tính
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  SĐT
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedCandidates.map((candidate) =>
              <tr
                key={candidate.id}
                className="hover:bg-slate-50 transition-colors">
                
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {candidate.cccd}
                  </td>
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">
                    {candidate.hoTen}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {candidate.ngaySinh}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {candidate.gioiTinh}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {candidate.sdt}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                      onClick={() => handleEdit(candidate)}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded transition-colors">
                      
                        <EditIcon size={18} />
                      </button>
                      <button
                      onClick={() => handleDelete(candidate.id)}
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

        <Pagination
          currentPage={currentPage}
          totalItems={filteredCandidates.length}
          itemsPerPage={itemsPerPage}
          onPageChange={setCurrentPage} />
        
      </div>

      {isModalOpen &&
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl mx-4">
            <div className="p-6 border-b border-slate-200">
              <h2 className="text-xl font-bold text-slate-800">
                {editingCandidate ? 'Chỉnh sửa thí sinh' : 'Thêm thí sinh mới'}
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
                    Ngày sinh
                  </label>
                  <input
                  type="date"
                  required
                  value={formData.ngaySinh}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    ngaySinh: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Giới tính
                  </label>
                  <select
                  required
                  value={formData.gioiTinh}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    gioiTinh: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                  
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 mb-6">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Số điện thoại
                  </label>
                  <input
                  type="tel"
                  required
                  value={formData.sdt}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    sdt: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Địa chỉ
                  </label>
                  <input
                  type="text"
                  required
                  value={formData.diaChi}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    diaChi: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                
                </div>
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
        columns={['CCCD', 'Họ tên', 'Ngày sinh', 'Giới tính', 'Địa chỉ', 'SĐT']}
        title="Import danh sách thí sinh" />
      
    </div>);

}