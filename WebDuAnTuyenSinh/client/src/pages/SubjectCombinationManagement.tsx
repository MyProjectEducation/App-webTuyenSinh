import React, { useState } from 'react';
import { useAppContext, SubjectCombination } from '../context/AppContext';
import { subjectComboService } from '../services/subjectComboService';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon, Loader2, Search } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import Pagination from '../components/Pagination';
export function SubjectCombinationManagement() {
  const { subjectCombinations, setSubjectCombinations, isLoading } = useAppContext();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [editingCombo, setEditingCombo] = useState<SubjectCombination | null>(null);
  
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  
  const [formData, setFormData] = useState({
    maToHop: '',
    tenToHop: '',
    mon1: '',
    mon2: '',
    mon3: ''
  });
  const handleAdd = () => {
    setEditingCombo(null);
    setFormData({
      maToHop: '',
      tenToHop: '',
      mon1: '',
      mon2: '',
      mon3: ''
    });
    setIsModalOpen(true);
  };
  const handleEdit = (combo: SubjectCombination) => {
    setEditingCombo(combo);
    setFormData({
      maToHop: combo.maToHop,
      tenToHop: combo.tenToHop,
      mon1: combo.mon1,
      mon2: combo.mon2,
      mon3: combo.mon3
    });
    setIsModalOpen(true);
  };
  const handleDelete = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa tổ hợp này?')) {
      try {
        await subjectComboService.delete(id);
        setSubjectCombinations(subjectCombinations.filter((c) => c.id !== id));
      } catch (err) {
        alert("Lỗi khi xoá");
      }
    }
  };
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingCombo) {
        await subjectComboService.update(editingCombo.id, formData);
        fetchSubjectCombinations();
      } else {
        await subjectComboService.create(formData);
        fetchSubjectCombinations();
      }
      setIsModalOpen(false);
    } catch (err) {
      alert("Lỗi lưu dữ liệu");
    }
  };
  const fetchSubjectCombinations = async () => {
    try {
      const data = await subjectComboService.getAll();
      setSubjectCombinations(data);
    } catch (err) {
      console.error(err);
    }
  };
  const handleImport = async (file: File) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const result = await subjectComboService.importSubjectCombos(formData);
      alert(result.message);
      
      fetchSubjectCombinations();
      setIsImportOpen(false);
    } catch (error: any) {
      console.error('Lỗi import:', error);
      alert(error.response?.data?.message || 'Lỗi khi import file Excel');
    }
  };

  const handleDownloadTemplate = () => {
    const headers = ['Mã tổ hợp', 'Tên tổ hợp', 'Môn 1', 'Môn 2', 'Môn 3'];
    const sampleData = ['A00', 'Toán, Vật lí, Hóa học', 'TO', 'LI', 'HO'];
    const csvContent = headers.join(',') + '\n' + sampleData.join(',');
    const blob = new Blob(["\ufeff", csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", "Template_ToHopMon.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Pagination logic
  React.useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

  const filteredCombos = React.useMemo(() => {
    if (!searchTerm) return subjectCombinations;
    return subjectCombinations.filter(c => 
      c.tenToHop?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.maToHop?.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [subjectCombinations, searchTerm]);

  const totalItems = filteredCombos.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  
  const currentDataChunk = React.useMemo(() => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    return filteredCombos.slice(startIndex, startIndex + itemsPerPage);
  }, [filteredCombos, currentPage]);

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Quản lý tổ hợp môn
        </h1>
        <p className="text-slate-600">Danh sách các tổ hợp môn xét tuyển</p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <h2 className="text-lg font-semibold text-slate-800">
              Danh sách tổ hợp
            </h2>
            <div className="flex flex-col sm:flex-row gap-3">
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Search className="h-5 w-5 text-slate-400" />
                </div>
                <input
                  type="text"
                  placeholder="Tìm tổ hợp..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 w-full sm:w-64"
                />
              </div>
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
                Thêm tổ hợp
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
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Mã tổ hợp
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Tên tổ hợp
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Môn 1
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Môn 2
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Môn 3
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {currentDataChunk.map((combo) =>
              <tr
                key={combo.id}
                className="hover:bg-slate-50 transition-colors">
                
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">
                    {combo.maToHop}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {combo.tenToHop}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {combo.mon1}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {combo.mon2}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {combo.mon3}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                      onClick={() => handleEdit(combo)}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded transition-colors">
                      
                        <EditIcon size={18} />
                      </button>
                      <button
                      onClick={() => handleDelete(combo.id)}
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
          totalPages={totalPages}
          totalItems={totalItems}
          itemsPerPage={itemsPerPage}
          onPageChange={setCurrentPage}
        />
      </div>

      {isModalOpen &&
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
            <div className="p-6 border-b border-slate-200">
              <h2 className="text-xl font-bold text-slate-800">
                {editingCombo ? 'Chỉnh sửa tổ hợp' : 'Thêm tổ hợp mới'}
              </h2>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Mã tổ hợp
                </label>
                <input
                type="text"
                required
                value={formData.maToHop}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  maToHop: e.target.value
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="VD: A00" />
              
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Tên tổ hợp
                </label>
                <input
                type="text"
                required
                value={formData.tenToHop}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  tenToHop: e.target.value
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="VD: Toán, Lý, Hóa" />
              
              </div>

              <div className="grid grid-cols-3 gap-4 mb-6">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Môn 1
                  </label>
                  <input
                  type="text"
                  required
                  value={formData.mon1}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    mon1: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Môn 2
                  </label>
                  <input
                  type="text"
                  required
                  value={formData.mon2}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    mon2: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
                
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Môn 3
                  </label>
                  <input
                  type="text"
                  required
                  value={formData.mon3}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    mon3: e.target.value
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
        onDownloadTemplate={handleDownloadTemplate}
        title="Import danh sách tổ hợp môn" />
      
    </div>);

}