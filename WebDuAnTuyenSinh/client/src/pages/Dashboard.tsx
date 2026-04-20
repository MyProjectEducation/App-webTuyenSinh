import React, { useState, useEffect } from 'react';
import { useAppContext } from '../context/AppContext';
import { Database, Sliders, Users, CheckSquare, Loader2, PlayCircle, RefreshCcw } from 'lucide-react';
import { candidateService } from '../services/candidateService';
import { majorService } from '../services/majorService';
import { subjectComboService } from '../services/subjectComboService';
import { majorComboService } from '../services/majorComboService';

export function Dashboard({ onNavigate }: { onNavigate?: (screen: string) => void }) {
  const { majors, subjectCombinations, candidateScores, candidates, admissionResults, setCandidates, setMajors, setSubjectCombinations, setCandidateScores } = useAppContext();
  const [isGenerating, setIsGenerating] = useState(false);

  const generateMockData = async () => {
    setIsGenerating(true);
    try {
      // 1. Tạo 2 Ngành mẫu
      if (majors.length === 0) {
          await majorService.create({ maNganh: '7480201', tenNganh: 'Công nghệ thông tin', toHopGoc: 'A00', chiTieu: 100, diemSan: 18, tuyenThang: '0', slXtt: 0, slDgnl: 50, slThpt: 50, slVsat: 0 });
          await majorService.create({ maNganh: '7480202', tenNganh: 'Kỹ thuật phần mềm', toHopGoc: 'A01', chiTieu: 80, diemSan: 19, tuyenThang: '0', slXtt: 0, slDgnl: 30, slThpt: 50, slVsat: 0 });
      }

      // 2. Tạo Tổ hợp
      if (subjectCombinations.length === 0) {
          await subjectComboService.create({ maToHop: 'A00', tenToHop: 'Toán, Lý, Hóa', mon1: 'Toan', mon2: 'Ly', mon3: 'Hoa' });
          await subjectComboService.create({ maToHop: 'A01', tenToHop: 'Toán, Lý, Anh', mon1: 'Toan', mon2: 'Ly', mon3: 'Anh' });
      }

      // 3. Thiệp lập liên kết Ngành - Tổ hợp (Bỏ qua vì frontend mockup này giả lập tốc độ, ta chỉ fake những thứ thiết yếu nhất)

      // 4. Tạo 10 Thí sinh & Điểm
      for (let i = 1; i <= 5; i++) {
        const cccd = `079201${Math.floor(100000 + Math.random() * 900000)}`;
        await candidateService.create({
            cccd, soBaoDanh: `SBD${i}`, ho: `Thí sinh`, ten: `Mẫu ${i}`, ngaySinh: '2005-01-01', dienThoai: '0901234567', gioiTinh: 'Nam', email: `ts${i}@gmail.com`, noiSinh: 'HCM', doiTuong: 'NONE', khuVuc: 'KV3', password: '123'
        });
      }

      alert("Tạo dữ liệu đồng bộ thành công! Vui lòng F5 (Làm mới nền tảng).");
    } catch (err) {
      console.error(err);
      alert("Quá trình mô phỏng gặp một số hạn chế về tốc độ tải mạng cục bộ. Một số dữ liệu đã được nạp.");
    } finally {
      setIsGenerating(false);
    }
  };

  const getStatus = () => {
    if (admissionResults.length > 0) return "Hoàn tất Xét Tuyển";
    if (candidates.length > 0 && candidateScores.length > 0) return "Sẵn sàng chạy Máy chủ lặp";
    if (majors.length > 0) return "Đang chờ Dữ liệu đầu vào";
    return "Đang chờ Thiết lập Cấu trúc";
  };

  const getProgress = () => {
    let p = 0;
    if (majors.length > 0) p += 25;
    if (subjectCombinations.length > 0) p += 25;
    if (candidates.length > 0) p += 25;
    if (admissionResults.length > 0) p += 25;
    return p;
  };

  return (
    <div className="min-h-screen bg-[#111827] text-slate-200 p-8 flex flex-col items-center justify-center font-sans">
      <div className="w-full max-w-5xl mb-12 flex justify-between items-end">
        <div>
          <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-emerald-400">
            SGU Admission Roadmap
          </h1>
          <p className="text-slate-400 mt-2 text-sm">Hệ thống điều phối luồng Tuyển sinh ĐH Sài Gòn</p>
        </div>
        <div className="flex gap-8 text-right">
          <div>
            <p className="text-xs text-slate-500 font-bold tracking-widest uppercase">Tiến độ Tổng</p>
            <p className="text-3xl font-bold text-emerald-400">{getProgress()}%</p>
          </div>
          <div>
             <p className="text-xs text-slate-500 font-bold tracking-widest uppercase">Thí sinh</p>
             <p className="text-3xl font-bold text-white">{candidates.length}</p>
          </div>
          <div>
            <p className="text-xs text-slate-500 font-bold tracking-widest uppercase">Trạng Thái</p>
            <p className="text-xl font-bold text-amber-400 mt-2">{getStatus()}</p>
          </div>
        </div>
      </div>

      <div className="relative w-full max-w-5xl bg-[#1F2937] rounded-[2rem] p-12 border border-slate-700/50 shadow-2xl">
        
        {/* Decorative Dotted Path */}
        <svg className="absolute inset-0 w-full h-full pointer-events-none opacity-20" xmlns="http://www.w3.org/2000/svg">
            <path d="M 280 180 Q 500 180 500 300 T 720 300" fill="transparent" stroke="white" strokeWidth="4" strokeDasharray="12 12" strokeLinecap="round" />
            <path d="M 280 430 Q 500 430 500 550 T 720 550" fill="transparent" stroke="white" strokeWidth="4" strokeDasharray="12 12" strokeLinecap="round" />
        </svg>

        <div className="grid grid-cols-2 gap-16 relative z-10">
          
          {/* Card 1 */}
          <div 
            onClick={() => onNavigate && onNavigate('majors')}
            className="bg-[#111827] rounded-3xl p-8 border border-white/5 hover:border-blue-500/50 transition-all cursor-pointer group hover:-translate-y-1 hover:shadow-[0_0_30px_rgba(59,130,246,0.15)] flex flex-col justify-between"
          >
            <div>
              <div className="flex items-center gap-4 mb-6">
                <div className="p-3 bg-blue-500/10 rounded-xl text-blue-400 group-hover:scale-110 transition-transform">
                  <Database size={32} />
                </div>
                <div>
                  <h3 className="text-xl font-bold text-white">Cấu trúc Master Data</h3>
                  <p className="text-slate-400 text-sm">Ngành, Tổ hợp & Liên kết</p>
                </div>
              </div>
              <div className="space-y-3 mb-8">
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-400">Ngành</span>
                  <span className="text-xl font-bold text-white">{majors.length}</span>
                </div>
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-400">Tổ hợp</span>
                  <span className="text-xl font-bold text-white">{subjectCombinations.length}</span>
                </div>
              </div>
            </div>
            <div>
              <span className={`px-4 py-1.5 rounded-full text-xs font-bold tracking-wider ${majors.length > 0 ? 'bg-emerald-500/20 text-emerald-400' : 'bg-slate-800 text-slate-500'}`}>
                {majors.length > 0 ? 'ĐÃ THIẾT LẬP' : 'CHỜ THIẾT LẬP'}
              </span>
            </div>
          </div>

          {/* Card 2 */}
          <div 
            onClick={() => onNavigate && onNavigate('scores')}
            className="bg-[#111827] rounded-3xl p-8 border border-white/5 hover:border-purple-500/50 transition-all cursor-pointer group hover:-translate-y-1 hover:shadow-[0_0_30px_rgba(168,85,247,0.15)] flex flex-col justify-between"
          >
            <div>
              <div className="flex items-center gap-4 mb-6">
                 <div className="p-3 bg-purple-500/10 rounded-xl text-purple-400 group-hover:scale-110 transition-transform">
                  <Sliders size={32} />
                </div>
                <div>
                  <h3 className="text-xl font-bold text-white">Cấu hình Engine</h3>
                  <p className="text-slate-400 text-sm">Quy đổi V-SAT & Ngoại ngữ</p>
                </div>
              </div>
              <div className="space-y-3 mb-8">
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-400">Bảng quy đổi</span>
                  <span className="text-xl font-bold text-white">Ready</span>
                </div>
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-400">Hệ số & Luật duyệt</span>
                  <span className="text-xl font-bold text-white">ON</span>
                </div>
              </div>
            </div>
            <div>
              <span className={`px-4 py-1.5 rounded-full text-xs font-bold tracking-wider bg-purple-500/20 text-purple-400`}>
                HOẠT ĐỘNG
              </span>
            </div>
          </div>

          {/* Card 3 */}
          <div 
            onClick={() => onNavigate && onNavigate('candidates')}
            className="bg-[#111827] rounded-3xl p-8 border border-white/5 hover:border-amber-500/50 transition-all cursor-pointer group hover:-translate-y-1 hover:shadow-[0_0_30px_rgba(245,158,11,0.15)] flex flex-col justify-between"
          >
            <div>
               <div className="flex items-center gap-4 mb-6">
                 <div className="p-3 bg-amber-500/10 rounded-xl text-amber-400 group-hover:scale-110 transition-transform">
                  <Users size={32} />
                </div>
                <div>
                  <h3 className="text-xl font-bold text-white">Dữ liệu Đầu vào</h3>
                  <p className="text-slate-400 text-sm">Thí sinh & Điểm thi</p>
                </div>
              </div>
              <div className="space-y-3 mb-8">
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-400">Thí sinh</span>
                  <span className="text-xl font-bold text-white">{candidates.length}</span>
                </div>
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-400">Đầu điểm</span>
                  <span className="text-xl font-bold text-white">{candidateScores.length}</span>
                </div>
              </div>
            </div>
            <div>
              <span className={`px-4 py-1.5 rounded-full text-xs font-bold tracking-wider ${candidates.length > 0 ? 'bg-amber-500/20 text-amber-400' : 'bg-slate-800 text-slate-500'}`}>
                {candidates.length > 0 ? 'ĐÃ NẠP DỮ LIỆU' : 'TRỐNG'}
              </span>
            </div>
          </div>

          {/* Card 4 */}
          <div 
             onClick={() => onNavigate && onNavigate('admission')}
             className="bg-[#111827] rounded-3xl p-8 border border-white/5 hover:border-emerald-500/50 transition-all cursor-pointer group hover:-translate-y-1 hover:shadow-[0_0_30px_rgba(16,185,129,0.15)] flex flex-col justify-between"
          >
            <div>
              <div className="flex items-center gap-4 mb-6">
                 <div className="p-3 bg-emerald-500/10 rounded-xl text-emerald-400 group-hover:scale-110 transition-transform">
                  <CheckSquare size={32} />
                </div>
                <div>
                  <h3 className="text-xl font-bold text-white">Xét tuyển & Kết quả</h3>
                  <p className="text-slate-400 text-sm">Lọc ảo & Chốt điểm chuẩn</p>
                </div>
              </div>
              <div className="space-y-3 mb-8">
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-400">Trúng tuyển</span>
                  <span className="text-xl font-bold text-white">{admissionResults.filter(r => r.trangThai === 'Đậu').length}</span>
                </div>
                <div className="flex justify-between items-center text-sm">
                  <span className="text-slate-400">Trượt nguyện vọng</span>
                  <span className="text-xl font-bold text-white">{admissionResults.filter(r => r.trangThai !== 'Đậu').length}</span>
                </div>
              </div>
            </div>
            <div>
              <span className={`px-4 py-1.5 rounded-full text-xs font-bold tracking-wider ${admissionResults.length > 0 ? 'bg-emerald-500/20 text-emerald-400' : 'bg-slate-800 text-slate-500'}`}>
                {admissionResults.length > 0 ? 'HOÀN TẤT VẬN HÀNH' : 'CHƯA CHẠY'}
              </span>
            </div>
          </div>
        </div>

        <div className="text-center mt-12 text-slate-500 text-sm">
          Dùng chuột di chuyển lên các thẻ để xem chi tiết hoặc nhấn nút Tạo dữ liệu Demo để bắt đầu
        </div>
      </div>

      <div className="flex gap-6 mt-12 w-full max-w-5xl">
        <button 
          onClick={generateMockData}
          disabled={isGenerating}
          className="flex-1 bg-[#2D3748] hover:bg-[#3A4556] text-white py-4 rounded-2xl font-bold text-lg flex items-center justify-center gap-3 transition-colors disabled:opacity-50"
        >
          {isGenerating ? <Loader2 className="animate-spin" /> : <PlayCircle />}
          {isGenerating ? 'ĐANG TẠO...' : 'Tạo dữ liệu Demo'}
        </button>
        <button 
          onClick={() => window.location.reload()}
          className="flex-1 bg-[#2D3748] hover:bg-[#3A4556] text-white py-4 rounded-2xl font-bold text-lg flex items-center justify-center gap-3 transition-colors"
        >
          <RefreshCcw />
          Làm mới
        </button>
      </div>

    </div>
  );
}