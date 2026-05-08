import React, { useState, useMemo } from 'react';
import { useAppContext, BonusPoint } from '../context/AppContext';
import { bonusService } from '../services/bonusService';
import { PlusIcon, EditIcon, TrashIcon, UploadIcon, Loader2, SaveIcon, Search } from 'lucide-react';
import { ImportModal } from '../components/ImportModal';
import Pagination from '../components/Pagination';

export function BonusPointManagement() {
  const { 
    bonusPoints, setBonusPoints, 
    candidates, majors, majorCombinations, subjectCombinations, 
    preferences, candidateScores,
    isLoading 
  } = useAppContext();
  
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 15;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);

  // Form State
  const [cccd, setCccd] = useState('');
  const [manganh, setManganh] = useState('');
  const [matohop, setMatohop] = useState('');
  const [phuongthuc, setPhuongthuc] = useState('THPT');
  const [khuvuc, setKhuvuc] = useState('KV3');
  const [doituong, setDoituong] = useState('NONE');
  const [chungchi, setChungchi] = useState('NONE');
  const [giaiHsg, setGiaiHsg] = useState('NONE');
  const [monHsg, setMonHsg] = useState('NONE');

  React.useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

  const filteredPoints = useMemo(() => {
    if (!searchTerm) return bonusPoints;
    return bonusPoints.filter(p => 
      p.cccd?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.hoTen?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.maNganh?.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [bonusPoints, searchTerm]);

  // Pagination
  const totalItems = filteredPoints.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  const paginatedPoints = useMemo(() => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    return filteredPoints.slice(startIndex, startIndex + itemsPerPage);
  }, [filteredPoints, currentPage]);

  const stats = useMemo(() => {
    return {
      count: bonusPoints.length,
      avg: bonusPoints.length > 0 ? (bonusPoints.reduce((acc, cur) => acc + cur.diem, 0) / bonusPoints.length).toFixed(2) : 0,
      thptCount: bonusPoints.filter(p => p.phuongThuc === 'THPT').length,
    };
  }, [bonusPoints]);

  const candidateInfo = useMemo(() => {
    const search = String(cccd).trim();
    if (!search) return null;
    return candidates.find(c => String(c.cccd).trim() === search || String(c.soBaoDanh).trim() === search);
  }, [cccd, candidates]);

  React.useEffect(() => {
    if (candidateInfo) {
      // Auto-fill khuvuc and doituong
      if (candidateInfo.khuVuc) setKhuvuc(candidateInfo.khuVuc);
      if (candidateInfo.doiTuong) setDoituong(candidateInfo.doiTuong);
      
      // Auto-select phuongthuc based on candidate's scores
      const cScores = candidateScores.filter(s => s.cccd === candidateInfo.cccd);
      if (cScores.length > 0) {
        // Find if they have VSAT or DGNL, prefer those over THPT
        const methods = new Set(cScores.map(s => s.loaiDiem));
        if (methods.has('VSAT')) setPhuongthuc('VSAT');
        else if (methods.has('DGNL')) setPhuongthuc('DGNL');
        else if (methods.has('THPT')) setPhuongthuc('THPT');
      }
    }
  }, [candidateInfo, candidateScores]);

  // Filter majors to ONLY those the candidate preferred
  const validMajors = useMemo(() => {
    if (!candidateInfo) return majors;
    const cPrefs = preferences.filter(p => p.cccd === candidateInfo.cccd).map(p => p.maNganh);
    if (cPrefs.length === 0) return majors; // fallback to all if no prefs yet
    // Unique list
    const uniquePrefs = Array.from(new Set(cPrefs));
    return majors.filter(m => uniquePrefs.includes(m.maNganh));
  }, [candidateInfo, preferences, majors]);

  const validCombos = useMemo(() => {
    if (!manganh) return [];
    const validMaToHop = majorCombinations.filter(mc => mc.maNganh === manganh).map(mc => mc.maToHop);
    return subjectCombinations.filter(sc => validMaToHop.includes(sc.maToHop));
  }, [manganh, majorCombinations, subjectCombinations]);

  const calculatedBonus = useMemo(() => {
    let ut = 0;
    if (khuvuc === 'KV2') ut += 0.25;
    if (khuvuc === 'KV2-NT') ut += 0.5;
    if (khuvuc === 'KV1') ut += 0.75;
    
    if (['DT01','DT02','DT03','DT04'].includes(doituong)) ut += 2.0;
    else if (['DT05','DT06','DT07'].includes(doituong)) ut += 1.0;

    let cc = 0;
    if (chungchi === 'IELTS 5.5') cc = 1.0;
    if (chungchi === 'IELTS 6.0') cc = 1.5;
    if (chungchi === 'IELTS 7.0') cc = 2.0;

    let hsg = 0;
    if (giaiHsg === 'NHAT') hsg = 2.0;
    if (giaiHsg === 'NHI') hsg = 1.5;
    if (giaiHsg === 'BA') hsg = 1.0;
    if (giaiHsg === 'KK') hsg = 0.5;

    // Simulate match logic
    const selectedCombo = validCombos.find(c => c.maToHop === matohop);
    let isMatched = false;
    let fallbackMsg = "";
    if (giaiHsg !== 'NONE' && monHsg !== 'NONE') {
      if (selectedCombo && (selectedCombo.mon1 === monHsg || selectedCombo.mon2 === monHsg || selectedCombo.mon3 === monHsg)) {
        isMatched = true;
      } else {
        hsg = hsg * 0.5; // Điểm ko môn đạt giải = 1/2 điểm có môn đạt giải
        fallbackMsg = "Không khớp môn giải với tổ hợp";
      }
    }

    const totalRaw = ut + cc + hsg;
    const isCapped = totalRaw > 3.0;
    const finalScore = Math.min(3.0, totalRaw);

    let note = `KV: ${ut > 0 ? '+'+ut : 0}`;
    if (doituong !== 'NONE') note += ` | ĐT: +${['DT01','DT02','DT03','DT04'].includes(doituong) ? 2.0 : 1.0}`;
    if (chungchi !== 'NONE') note += ` | NN: +${cc}`;
    if (giaiHsg !== 'NONE') note += ` | HSG: +${hsg} ${isMatched ? '(Khớp Tổ hợp)' : '(Trái tuyến)'}`;

    return { ut, cc, hsg, totalRaw, finalScore, isCapped, note, isMatched };
  }, [khuvuc, doituong, chungchi, giaiHsg, monHsg, matohop, validCombos]);

  const handleAdd = () => {
    setCccd('');
    setManganh('');
    setMatohop('');
    setPhuongthuc('THPT');
    setKhuvuc('KV3');
    setDoituong('NONE');
    setChungchi('NONE');
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (confirm('Bạn có chắc chắn muốn xóa hệ thống điểm cộng ngữ cảnh này?')) {
      try {
        await bonusService.delete(id);
        setBonusPoints(bonusPoints.filter((p) => p.id !== id));
      } catch (err) {
        alert("Lỗi xóa dữ liệu");
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!candidateInfo) {
      alert("Mã CCCD không hợp lệ hoặc chưa tồn tại hệ thống thí sinh.");
      return;
    }

    try {
      const payload = {
        cccd, manganh, matohop, phuongthuc, khuvuc, doituong, chungchi
      };
      
      const result = await bonusService.create(payload) as any;
      
      const newDiemTong = result.diemTong || 0;
      const newDiemC = result.diemC || 0;
      const newDiemUt = result.diemUt || 0;
      const newGhiChu = result.ghiChu || '';
      
      const dc_keys = `${cccd}_${manganh}_${matohop}`;

      // Xóa điểm cũ (nếu trùng khóa dc_keys) và thêm điểm mới vào State thay vì refetch
      const filtered = bonusPoints.filter(p => p.id !== dc_keys && !(p.cccd === cccd && p.maNganh === manganh && p.maToHop === matohop));
      
      const newPoint: BonusPoint = {
        id: dc_keys,
        cccd: cccd,
        hoTen: `${candidateInfo.ho} ${candidateInfo.ten}`,
        maNganh: manganh,
        maToHop: matohop,
        phuongThuc: phuongthuc,
        loaiDiemCong: phuongthuc,
        diem: newDiemTong,
        diemC: newDiemC,
        diemUt: newDiemUt,
        ghiChu: newGhiChu
      };
      setBonusPoints([...filtered, newPoint]);
      setIsModalOpen(false);
    } catch (err: any) {
      alert(err.response?.data?.error || "Lỗi thao tác máy chủ");
    }
  };

  const fetchBonusPoints = async () => {
    try {
      const data = await bonusService.getAll();
      setBonusPoints(data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleImport = async (file: File) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const result = await bonusService.importBonusPoints(formData);
      alert(result.message);
      
      fetchBonusPoints();
      setIsImportOpen(false);
    } catch (error: any) {
      console.error('Lỗi import:', error);
      alert(error.response?.data?.message || 'Lỗi khi import file Excel');
    }
  };

  const handleDownloadTemplate = () => {
    const headers = ['CCCD', 'Mã ngành', 'Mã tổ hợp', 'Phương thức', 'Khu vực', 'Đối tượng', 'Chứng chỉ'];
    const sampleData = ['012345678901', '7480201', 'A00', 'THPT', 'KV1', 'DT01', 'IELTS 6.0'];
    const csvContent = headers.join(',') + '\n' + sampleData.join(',');
    const blob = new Blob(["\ufeff", csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", "Template_DiemCong.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="p-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">Quản lý điểm cộng xét tuyển</h1>
        <p className="text-slate-600">Quản lý điểm ưu tiên, chứng chỉ ngoại ngữ dựa trên ngữ cảnh Ngành & Tổ hợp & Phương thức</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Tổng số Hồ sơ điểm cộng (Records)</p>
          <p className="text-2xl font-bold text-slate-800">{stats.count}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Điểm cộng Trung bình</p>
          <p className="text-2xl font-bold text-slate-800">{stats.avg}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-5">
          <p className="text-sm font-medium text-slate-500 mb-1">Đã áp dụng thang THPT</p>
          <p className="text-2xl font-bold text-slate-800">{stats.thptCount}</p>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-md border border-slate-200">
        <div className="p-6 border-b border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <h2 className="text-lg font-semibold text-slate-800">Danh sách tổng hợp</h2>
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-slate-400" />
              </div>
              <input
                type="text"
                placeholder="Tìm CCCD, Tên, Ngành..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 w-full sm:w-64"
              />
            </div>
            <button onClick={() => setIsImportOpen(true)} className="flex items-center gap-2 border border-slate-300 hover:bg-slate-50 text-slate-700 px-4 py-2 rounded-lg font-medium transition-colors">
              <UploadIcon size={20} />
              Import
            </button>
            <button onClick={handleAdd} className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow">
              <PlusIcon size={20} />
              Thêm điểm cộng mới
            </button>
          </div>
        </div>

        <div className="overflow-x-auto relative min-h-[200px]">
          {isLoading && (
            <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
              <Loader2 className="animate-spin text-blue-600" size={32} />
              <span className="ml-2 text-slate-600">Đang tải dữ liệu...</span>
            </div>
          )}
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">CCCD</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Họ tên</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Ngữ cảnh (Ngành - Môn - PT)</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Tổng (+điểm)</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Chi tiết Ghi chú</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {paginatedPoints.map((point) => (
                <tr key={point.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-6 py-4 text-sm text-slate-800">{point.cccd}</td>
                  <td className="px-6 py-4 text-sm font-medium text-slate-800">{point.hoTen}</td>
                  <td className="px-6 py-4 text-sm text-slate-800">
                    <div className="flex flex-col gap-1">
                      <span className="font-semibold text-blue-800">{point.maNganh}</span>
                      <div className="flex gap-2">
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-600">{point.maToHop}</span>
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-purple-100 text-purple-700">{point.phuongThuc}</span>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm font-bold text-green-600">+{point.diem}</td>
                  <td className="px-6 py-4 text-sm text-slate-500 italic max-w-xs truncate">{point.ghiChu}</td>
                  <td className="px-6 py-4 text-sm">
                    <button onClick={() => handleDelete(point.id)} className="p-2 text-red-600 hover:bg-red-50 rounded transition-colors" title="Xóa">
                      <TrashIcon size={18} />
                    </button>
                  </td>
                </tr>
              ))}
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

      {isModalOpen && (
        <div className="fixed inset-0 bg-slate-900/70 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-6xl max-h-[95vh] flex flex-col">
            <div className="p-6 border-b border-slate-100 sticky top-0 bg-white/95 backdrop-blur-sm z-10 flex justify-between items-center rounded-t-2xl">
              <div>
                <h2 className="text-2xl font-bold text-slate-800">Cấu Hình Điểm Cộng Thông Minh</h2>
                <p className="text-sm text-slate-500 mt-1">Context-Aware Bonus Points Form - Tự động nhận diện ngữ cảnh và áp trần</p>
              </div>
              <button onClick={() => setIsModalOpen(false)} className="p-2 hover:bg-slate-100 rounded-full transition-colors">
                 <svg className="w-6 h-6 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"></path></svg>
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 overflow-y-auto flex-1">
              <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
                
                {/* CỘT TRÁI: LỰA CHỌN CẤU HÌNH (8 columns) */}
                <div className="lg:col-span-8 space-y-6">
                  
                  {/* Ngữ cảnh xét tuyển */}
                  <div className="p-5 bg-blue-50/50 rounded-xl border border-blue-100">
                    <h4 className="text-blue-800 font-bold mb-4 flex items-center gap-2">
                       <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path></svg>
                       1. Ngữ cảnh Thí sinh
                    </h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-1">Mã CCCD thí sinh *</label>
                        <input 
                          type="text" required list="candidate-cccd-list" placeholder="Gõ CCCD hoặc SBD..."
                          value={cccd} onChange={(e) => setCccd(e.target.value)} 
                          className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 font-medium" 
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-1">Họ tên thí sinh</label>
                        <div className="w-full px-4 py-2.5 bg-slate-100 border border-slate-200 rounded-lg text-slate-600 font-semibold truncate">
                          {candidateInfo ? `${candidateInfo.ho} ${candidateInfo.ten}` : '---'}
                        </div>
                      </div>
                      <div className="md:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-semibold text-slate-700 mb-1">Ngành xét tuyển *</label>
                          <select required value={manganh} onChange={(e) => setManganh(e.target.value)} className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 font-medium bg-white">
                            <option value="">-- Chọn ngành (Theo NV đã ĐK) --</option>
                            {validMajors.map(m => <option key={m.id} value={m.maNganh}>{m.maNganh} - {m.tenNganh}</option>)}
                          </select>
                        </div>
                        <div>
                          <label className="block text-sm font-semibold text-slate-700 mb-1">Tổ hợp môn *</label>
                          <select required value={matohop} onChange={(e) => setMatohop(e.target.value)} disabled={!manganh} className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 font-medium disabled:bg-slate-100">
                            <option value="">-- Tổ hợp của ngành --</option>
                            {validCombos.map(c => <option key={c.id} value={c.maToHop}>{c.maToHop} - {c.tenToHop}</option>)}
                          </select>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Ưu tiên & Giải thưởng */}
                  <div className="p-5 bg-white rounded-xl border border-slate-200 shadow-sm">
                    <h4 className="text-slate-800 font-bold mb-4">2. Các tiêu chí cộng điểm</h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                      <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-1">Khu Vực Ưu Tiên</label>
                        <select value={khuvuc} onChange={(e) => setKhuvuc(e.target.value)} className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500">
                          <option value="KV3">KV3 (0đ)</option>
                          <option value="KV2">KV2 (+0.25đ)</option>
                          <option value="KV2-NT">KV2-NT (+0.5đ)</option>
                          <option value="KV1">KV1 (+0.75đ)</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-1">Đối Tượng Ưu Tiên</label>
                        <select value={doituong} onChange={(e) => setDoituong(e.target.value)} className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500">
                          <option value="NONE">Không có</option>
                          <option value="DT01">ĐT 01, 02, 03, 04 (+2.0đ)</option>
                          <option value="DT05">ĐT 05, 06, 07 (+1.0đ)</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-1">Chứng chỉ Ngoại Ngữ</label>
                        <select value={chungchi} onChange={(e) => setChungchi(e.target.value)} className="w-full px-4 py-2.5 border border-blue-200 bg-blue-50/50 rounded-lg focus:ring-2 focus:ring-blue-500 text-blue-800 font-medium">
                          <option value="NONE">Không có</option>
                          <option value="IELTS 5.5">IELTS 5.5 (Quy đổi 1.0đ)</option>
                          <option value="IELTS 6.0">IELTS 6.0 (Quy đổi 1.5đ)</option>
                          <option value="IELTS 7.0">IELTS ≥ 7.0 (Quy đổi 2.0đ)</option>
                        </select>
                      </div>
                      <div>
                         <label className="block text-sm font-semibold text-slate-700 mb-1">Giải HSG / KHKT</label>
                         <div className="flex gap-2">
                           <select value={giaiHsg} onChange={(e) => setGiaiHsg(e.target.value)} className="w-1/2 px-3 py-2.5 border border-amber-200 bg-amber-50/50 rounded-lg focus:ring-2 focus:ring-amber-500 text-amber-800 font-medium">
                             <option value="NONE">Không</option>
                             <option value="NHAT">Giải Nhất</option>
                             <option value="NHI">Giải Nhì</option>
                             <option value="BA">Giải Ba</option>
                             <option value="KK">Khuyến khích</option>
                           </select>
                           <select disabled={giaiHsg === 'NONE'} value={monHsg} onChange={(e) => setMonHsg(e.target.value)} className="w-1/2 px-3 py-2.5 border border-slate-300 rounded-lg disabled:bg-slate-100 disabled:text-slate-400">
                             <option value="NONE">Môn giải...</option>
                             <option value="TO">Toán</option>
                             <option value="LI">Vật lí</option>
                             <option value="HO">Hóa học</option>
                             <option value="N1">Tiếng Anh</option>
                             <option value="VA">Ngữ văn</option>
                             <option value="KHKT">KHKT (Hành vi)</option>
                           </select>
                         </div>
                      </div>
                    </div>
                  </div>
                </div>

                {/* CỘT PHẢI: XEM TRƯỚC KẾT QUẢ TÍNH TOÁN (4 columns) */}
                <div className="lg:col-span-4 flex flex-col gap-4">
                  <div className="bg-slate-900 text-white p-8 rounded-2xl shadow-xl flex-1 flex flex-col justify-center items-center text-center relative overflow-hidden">
                    {/* Background decoration */}
                    <div className="absolute top-0 right-0 -mr-8 -mt-8 w-32 h-32 rounded-full bg-blue-500 opacity-10 blur-2xl"></div>
                    <div className="absolute bottom-0 left-0 -ml-8 -mb-8 w-32 h-32 rounded-full bg-purple-500 opacity-10 blur-2xl"></div>
                    
                    <span className="text-slate-400 text-sm uppercase tracking-widest mb-4 font-semibold z-10">Tổng điểm cộng thực tế</span>
                    
                    <div className="text-7xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-cyan-300 mb-6 z-10">
                      {calculatedBonus.finalScore.toFixed(2)}
                    </div>
                    
                    {calculatedBonus.isCapped && (
                      <div className="px-4 py-2 bg-amber-500/20 border border-amber-500/50 rounded-full text-xs font-semibold text-amber-300 z-10 flex items-center gap-1.5 animate-pulse">
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
                        Áp trần quy chế (Tổng gốc: {calculatedBonus.totalRaw.toFixed(2)})
                      </div>
                    )}
                    
                    <div className="mt-8 pt-6 border-t border-slate-700/50 w-full z-10">
                       <p className="text-xs text-slate-400 mb-2 font-mono">{calculatedBonus.note}</p>
                       {giaiHsg !== 'NONE' && (
                          <p className={`text-sm italic mt-3 ${calculatedBonus.isMatched ? 'text-green-400' : 'text-amber-400'}`}>
                            {calculatedBonus.isMatched 
                              ? `"Hệ thống tự động nhận diện Môn giải khớp với Tổ hợp ${matohop || '...'}. Áp dụng 100% điểm thưởng."`
                              : `"Môn đạt giải không nằm trong Tổ hợp ${matohop || '...'}. Áp dụng 50% điểm thưởng."`}
                          </p>
                       )}
                    </div>
                  </div>
                </div>

              </div>
            </form>

            {/* ACTION FOOTER */}
            <div className="p-6 border-t border-slate-100 bg-slate-50 sticky bottom-0 z-10 rounded-b-2xl flex justify-end gap-3">
              <button type="button" onClick={() => setIsModalOpen(false)} className="px-6 py-3 font-medium text-slate-600 hover:bg-slate-200 rounded-xl transition-colors">
                Hủy bỏ
              </button>
              <button onClick={handleSubmit} className="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl transition-all shadow-lg hover:shadow-blue-500/30 flex items-center gap-2">
                <SaveIcon size={20} /> Lưu Kết Quả
              </button>
            </div>
          </div>
        </div>
      )}
      <ImportModal 
        isOpen={isImportOpen} 
        onClose={() => setIsImportOpen(false)} 
        onImport={handleImport} 
        onDownloadTemplate={handleDownloadTemplate}
        title="Import điểm cộng ưu tiên" 
      />
    </div>
  );
}