import React, { useState } from 'react';
import { useAppContext, ConversionRule } from '../context/AppContext';
import {
  EditIcon,
  TrashIcon,
  PlusIcon,
  UploadIcon,
  SearchIcon } from
'lucide-react';
import { ImportModal } from '../components/ImportModal';
import { Pagination } from '../components/Pagination';
export function ScoreConversion() {
  const { conversionRules, setConversionRules } = useAppContext();
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [editingRule, setEditingRule] = useState<ConversionRule | null>(null);
  const [formData, setFormData] = useState({
    khoiGoc: '',
    khoiDich: '',
    mucChenhLech: ''
  });
  const filteredRules = conversionRules.filter(
    (r) =>
    r.khoiGoc.toLowerCase().includes(searchTerm.toLowerCase()) ||
    r.khoiDich.toLowerCase().includes(searchTerm.toLowerCase())
  );
  const paginatedRules = filteredRules.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );
  const handleAdd = () => {
    setEditingRule(null);
    setFormData({
      khoiGoc: '',
      khoiDich: '',
      mucChenhLech: ''
    });
    setIsModalOpen(true);
  };
  const handleEdit = (rule: ConversionRule) => {
    setEditingRule(rule);
    setFormData({
      khoiGoc: rule.khoiGoc,
      khoiDich: rule.khoiDich,
      mucChenhLech: rule.mucChenhLech.toString()
    });
    setIsModalOpen(true);
  };
  const handleDelete = (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa quy tắc này?')) {
      setConversionRules(conversionRules.filter((r) => r.id !== id));
    }
  };
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const mucChenhLech = parseFloat(formData.mucChenhLech);
    if (editingRule) {
      setConversionRules(
        conversionRules.map((r) =>
        r.id === editingRule.id ?
        {
          ...r,
          ...formData,
          mucChenhLech
        } :
        r
        )
      );
    } else {
      const newRule: ConversionRule = {
        id: Date.now().toString(),
        khoiGoc: formData.khoiGoc,
        khoiDich: formData.khoiDich,
        mucChenhLech
      };
      setConversionRules([...conversionRules, newRule]);
    }
    setIsModalOpen(false);
  };
  const handleImport = (data: any[]) => {
    const newRules = data.map((row, index) => ({
      id: `imported-${Date.now()}-${index}`,
      khoiGoc: row['Khối gốc'] || '',
      khoiDich: row['Khối đích'] || '',
      mucChenhLech: parseFloat(row['Mức chênh lệch']) || 0
    }));
    setConversionRules([...conversionRules, ...newRules]);
  };
  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Bảng quy đổi điểm
        </h1>
        <p className="text-slate-600">
          Quản lý quy tắc chuyển đổi điểm giữa các khối thi
        </p>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="w-full sm:w-80 relative">
              <SearchIcon
                className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"
                size={20} />
              
              <input
                type="text"
                placeholder="Tìm khối gốc hoặc khối đích..."
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
                Thêm quy tắc
              </button>
            </div>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Khối thi gốc
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Khối thi đích
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Mức chênh lệch
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedRules.map((rule) =>
              <tr
                key={rule.id}
                className="hover:bg-slate-50 transition-colors">
                
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">
                    {rule.khoiGoc}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {rule.khoiDich}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <span
                    className={`font-medium ${rule.mucChenhLech >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                    
                      {rule.mucChenhLech > 0 ? '+' : ''}
                      {rule.mucChenhLech.toFixed(2)}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                      onClick={() => handleEdit(rule)}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded transition-colors">
                      
                        <EditIcon size={18} />
                      </button>
                      <button
                      onClick={() => handleDelete(rule.id)}
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
          totalItems={filteredRules.length}
          itemsPerPage={itemsPerPage}
          onPageChange={setCurrentPage} />
        

        <div className="p-6 bg-slate-50 border-t border-slate-200">
          <p className="text-sm text-slate-600">
            <strong>Lưu ý:</strong> Mức chênh lệch dương (+) nghĩa là cộng điểm,
            âm (-) nghĩa là trừ điểm khi quy đổi từ khối gốc sang khối đích.
          </p>
        </div>
      </div>

      {isModalOpen &&
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
            <div className="p-6 border-b border-slate-200">
              <h2 className="text-xl font-bold text-slate-800">
                {editingRule ? 'Chỉnh sửa quy tắc' : 'Thêm quy tắc mới'}
              </h2>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Khối thi gốc
                </label>
                <input
                type="text"
                required
                value={formData.khoiGoc}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  khoiGoc: e.target.value
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="VD: A01" />
              
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Khối thi đích
                </label>
                <input
                type="text"
                required
                value={formData.khoiDich}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  khoiDich: e.target.value
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="VD: A00" />
              
              </div>

              <div className="mb-6">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Mức chênh lệch
                </label>
                <input
                type="number"
                step="0.01"
                required
                value={formData.mucChenhLech}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  mucChenhLech: e.target.value
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
        columns={['Khối gốc', 'Khối đích', 'Mức chênh lệch']}
        title="Import bảng quy đổi" />
      
    </div>);

}