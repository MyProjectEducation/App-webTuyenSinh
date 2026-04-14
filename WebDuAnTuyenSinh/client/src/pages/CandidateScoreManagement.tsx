import React, { useMemo, useState } from 'react';
import { useAppContext, CandidateScore } from '../context/AppContext';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import { Pagination } from '../components/Pagination';
type ScoreType = 'THPT' | 'VSAT' | 'DGNL';
export function CandidateScoreManagement() {
  const { candidateScores, setCandidateScores } = useAppContext();
  const [activeTab, setActiveTab] = useState<ScoreType>('THPT');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 15;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [editingScore, setEditingScore] = useState<CandidateScore | null>(null);
  const [formData, setFormData] = useState({
    cccd: '',
    hoTen: '',
    loaiDiem: 'THPT' as ScoreType,
    mon: '',
    diem: ''
  });
  // Filter scores by active tab
  const filteredScores = candidateScores.filter((s) => s.loaiDiem === activeTab);
  // Pagination
  const paginatedScores = filteredScores.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );
  // Statistics
  const stats = useMemo(() => {
    const currentScores = filteredScores;
    const count = currentScores.length;
    // Calculate average score
    const avg =
    count > 0 ? currentScores.reduce((sum, s) => sum + s.diem, 0) / count : 0;
    // Group by subject
    const bySubject = currentScores.reduce(
      (acc, s) => {
        if (!acc[s.mon])
        acc[s.mon] = {
          total: 0,
          count: 0
        };
        acc[s.mon].total += s.diem;
        acc[s.mon].count += 1;
        return acc;
      },
      {} as Record<
        string,
        {
          total: number;
          count: number;
        }>

    );
    return {
      count,
      avg,
      bySubject
    };
  }, [filteredScores]);
  const handleAdd = () => {
    setEditingScore(null);
    setFormData({
      cccd: '',
      hoTen: '',
      loaiDiem: activeTab,
      mon: '',
      diem: ''
    });
    setIsModalOpen(true);
  };
  const handleEdit = (score: CandidateScore) => {
    setEditingScore(score);
    setFormData({
      cccd: score.cccd,
      hoTen: score.hoTen,
      loaiDiem: score.loaiDiem,
      mon: score.mon,
      diem: score.diem.toString()
    });
    setIsModalOpen(true);
  };
  const handleDelete = (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa điểm này?')) {
      setCandidateScores(candidateScores.filter((s) => s.id !== id));
    }
  };
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const diemNum = parseFloat(formData.diem);
    if (editingScore) {
      setCandidateScores(
        candidateScores.map((s) =>
        s.id === editingScore.id ?
        {
          ...s,
          ...formData,
          diem: diemNum
        } :
        s
        )
      );
    } else {
      const newScore: CandidateScore = {
        id: Date.now().toString(),
        cccd: formData.cccd,
        hoTen: formData.hoTen,
        loaiDiem: formData.loaiDiem,
        mon: formData.mon,
        diem: diemNum
      };
      setCandidateScores([...candidateScores, newScore]);
    }
    setIsModalOpen(false);
  };
  const handleImport = (data: any[]) => {
    const newScores = data.map((row, index) => ({
      id: `imported-${Date.now()}-${index}`,
      cccd: row['CCCD'] || '',
      hoTen: row['Họ tên'] || '',
      loaiDiem: (row['Loại điểm'] || 'THPT') as ScoreType,
      mon: row['Môn'] || '',
      diem: parseFloat(row['Điểm']) || 0
    }));
    setCandidateScores([...candidateScores, ...newScores]);
  };
  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          Quản lý điểm thí sinh
        </h1>
        <p className="text-slate-600">
          Quản lý điểm thi THPT, ĐGNL, và VSAT của thí sinh
        </p>
      </div>

      {/* Stats Section */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">
            Tổng số đầu điểm ({activeTab})
          </p>
          <p className="text-2xl font-bold text-slate-800">{stats.count}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">
            Điểm trung bình
          </p>
          <p className="text-2xl font-bold text-slate-800">
            {stats.avg.toFixed(2)}
          </p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Số môn thi</p>
          <p className="text-2xl font-bold text-slate-800">
            {Object.keys(stats.bySubject).length}
          </p>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-4 border-b border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex bg-slate-100 p-1 rounded-lg">
            {(['THPT', 'VSAT', 'DGNL'] as ScoreType[]).map((type) =>
            <button
              key={type}
              onClick={() => {
                setActiveTab(type);
                setCurrentPage(1);
              }}
              className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${activeTab === type ? 'bg-white text-blue-600 shadow-sm' : 'text-slate-600 hover:text-slate-800'}`}>
              
                Điểm {type}
              </button>
            )}
          </div>

          <div className="flex gap-3">
            <button
              onClick={() => setIsImportOpen(true)}
              className="flex items-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg text-sm font-medium transition-colors">
              
              <UploadIcon size={18} />
              Import
            </button>
            <button
              onClick={handleAdd}
              className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
              
              <PlusIcon size={18} />
              Thêm điểm
            </button>
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
                  Môn
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Điểm
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedScores.map((score) =>
              <tr
                key={score.id}
                className="hover:bg-slate-50 transition-colors">
                
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {score.cccd}
                  </td>
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">
                    {score.hoTen}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    {score.mon}
                  </td>
                  <td className="px-6 py-4 text-sm font-semibold text-blue-600">
                    {score.diem}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex items-center gap-2">
                      <button
                      onClick={() => handleEdit(score)}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded transition-colors">
                      
                        <EditIcon size={18} />
                      </button>
                      <button
                      onClick={() => handleDelete(score.id)}
                      className="p-2 text-red-600 hover:bg-red-50 rounded transition-colors">
                      
                        <TrashIcon size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              )}
              {paginatedScores.length === 0 &&
              <tr>
                  <td
                  colSpan={5}
                  className="px-6 py-8 text-center text-slate-500">
                  
                    Chưa có dữ liệu điểm {activeTab}
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <Pagination
          currentPage={currentPage}
          totalItems={filteredScores.length}
          itemsPerPage={itemsPerPage}
          onPageChange={setCurrentPage} />
        
      </div>

      {isModalOpen &&
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
            <div className="p-6 border-b border-slate-200">
              <h2 className="text-xl font-bold text-slate-800">
                {editingScore ? 'Chỉnh sửa điểm' : 'Thêm điểm mới'}
              </h2>
            </div>

            <form onSubmit={handleSubmit} className="p-6">
              <div className="mb-4">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Loại điểm
                </label>
                <select
                required
                value={formData.loaiDiem}
                onChange={(e) =>
                setFormData({
                  ...formData,
                  loaiDiem: e.target.value as ScoreType
                })
                }
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-slate-50"
                disabled={!!editingScore}>
                
                  <option value="THPT">THPT</option>
                  <option value="VSAT">VSAT</option>
                  <option value="DGNL">ĐGNL</option>
                </select>
              </div>

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

              <div className="grid grid-cols-2 gap-4 mb-6">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Môn thi
                  </label>
                  <input
                  type="text"
                  required
                  value={formData.mon}
                  onChange={(e) =>
                  setFormData({
                    ...formData,
                    mon: e.target.value
                  })
                  }
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder={
                  formData.loaiDiem === 'DGNL' ? 'VD: Tổng hợp' : 'VD: Toán'
                  } />
                
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Điểm
                  </label>
                  <input
                  type="number"
                  step="0.01"
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
        columns={['CCCD', 'Họ tên', 'Loại điểm', 'Môn', 'Điểm']}
        title="Import điểm thí sinh" />
      
    </div>);

}