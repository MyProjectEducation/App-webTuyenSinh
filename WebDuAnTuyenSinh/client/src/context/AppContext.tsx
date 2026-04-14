import React, { useState, createContext, useContext } from 'react';
export interface Candidate {
  id: string;
  cccd: string;
  hoTen: string;
  ngaySinh: string;
  gioiTinh: string;
  diaChi: string;
  sdt: string;
}
export interface Major {
  id: string;
  maNganh: string;
  tenNganh: string;
  chiTieu: number;
}
export interface ConversionRule {
  id: string;
  khoiGoc: string;
  khoiDich: string;
  mucChenhLech: number;
}
export interface AdmissionResult {
  candidateId: string;
  cccd: string;
  hoTen: string;
  diem: number;
  diemCong: number;
  tongDiem: number;
  nganhTrungTuyen: string;
  trangThai: 'Đậu' | 'Không đậu';
}
export interface SubjectCombination {
  id: string;
  maToHop: string;
  tenToHop: string;
  mon1: string;
  mon2: string;
  mon3: string;
}
export interface MajorCombination {
  id: string;
  maNganh: string;
  maToHop: string;
}
export interface CandidateScore {
  id: string;
  cccd: string;
  hoTen: string;
  loaiDiem: 'THPT' | 'VSAT' | 'DGNL';
  mon: string;
  diem: number;
}
export interface BonusPoint {
  id: string;
  cccd: string;
  hoTen: string;
  loaiDiemCong: string;
  diem: number;
  ghiChu: string;
}
export interface Preference {
  id: string;
  cccd: string;
  hoTen: string;
  thuTuNV: number;
  maNganh: string;
  maToHop: string;
}
interface AppContextType {
  candidates: Candidate[];
  setCandidates: (candidates: Candidate[]) => void;
  majors: Major[];
  setMajors: (majors: Major[]) => void;
  conversionRules: ConversionRule[];
  setConversionRules: (rules: ConversionRule[]) => void;
  admissionResults: AdmissionResult[];
  setAdmissionResults: (results: AdmissionResult[]) => void;
  subjectCombinations: SubjectCombination[];
  setSubjectCombinations: (combos: SubjectCombination[]) => void;
  majorCombinations: MajorCombination[];
  setMajorCombinations: (combos: MajorCombination[]) => void;
  candidateScores: CandidateScore[];
  setCandidateScores: (scores: CandidateScore[]) => void;
  bonusPoints: BonusPoint[];
  setBonusPoints: (points: BonusPoint[]) => void;
  preferences: Preference[];
  setPreferences: (prefs: Preference[]) => void;
}
const AppContext = createContext<AppContextType | undefined>(undefined);
export function AppProvider({ children }: {children: ReactNode;}) {
  const [candidates, setCandidates] = useState<Candidate[]>([
  {
    id: '1',
    cccd: '079201012345',
    hoTen: 'Nguyễn Văn An',
    ngaySinh: '2005-01-15',
    gioiTinh: 'Nam',
    diaChi: 'TP.HCM',
    sdt: '0901234567'
  },
  {
    id: '2',
    cccd: '079201054321',
    hoTen: 'Trần Thị Bình',
    ngaySinh: '2005-03-20',
    gioiTinh: 'Nữ',
    diaChi: 'Hà Nội',
    sdt: '0912345678'
  },
  {
    id: '3',
    cccd: '079201098765',
    hoTen: 'Lê Minh Châu',
    ngaySinh: '2005-05-10',
    gioiTinh: 'Nữ',
    diaChi: 'Đà Nẵng',
    sdt: '0923456789'
  },
  {
    id: '4',
    cccd: '079201011111',
    hoTen: 'Phạm Thị Dung',
    ngaySinh: '2005-07-25',
    gioiTinh: 'Nữ',
    diaChi: 'Cần Thơ',
    sdt: '0934567890'
  },
  {
    id: '5',
    cccd: '079201022222',
    hoTen: 'Hoàng Văn Em',
    ngaySinh: '2005-09-30',
    gioiTinh: 'Nam',
    diaChi: 'Hải Phòng',
    sdt: '0945678901'
  },
  {
    id: '6',
    cccd: '079201033333',
    hoTen: 'Võ Thị Phương',
    ngaySinh: '2005-11-05',
    gioiTinh: 'Nữ',
    diaChi: 'Nha Trang',
    sdt: '0956789012'
  },
  {
    id: '7',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    ngaySinh: '2005-02-14',
    gioiTinh: 'Nam',
    diaChi: 'Bình Dương',
    sdt: '0967890123'
  },
  {
    id: '8',
    cccd: '079201055555',
    hoTen: 'Bùi Thị Hạnh',
    ngaySinh: '2005-04-08',
    gioiTinh: 'Nữ',
    diaChi: 'Đồng Nai',
    sdt: '0978901234'
  },
  {
    id: '9',
    cccd: '079201066666',
    hoTen: 'Ngô Thanh Hùng',
    ngaySinh: '2005-06-22',
    gioiTinh: 'Nam',
    diaChi: 'Huế',
    sdt: '0989012345'
  },
  {
    id: '10',
    cccd: '079201077777',
    hoTen: 'Dương Thị Kim',
    ngaySinh: '2005-08-17',
    gioiTinh: 'Nữ',
    diaChi: 'Vũng Tàu',
    sdt: '0990123456'
  },
  {
    id: '11',
    cccd: '079201088888',
    hoTen: 'Lý Văn Long',
    ngaySinh: '2005-10-03',
    gioiTinh: 'Nam',
    diaChi: 'Long An',
    sdt: '0901122334'
  },
  {
    id: '12',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    ngaySinh: '2005-12-28',
    gioiTinh: 'Nữ',
    diaChi: 'Quảng Ninh',
    sdt: '0912233445'
  },
  {
    id: '13',
    cccd: '079202011111',
    hoTen: 'Trịnh Đức Nam',
    ngaySinh: '2005-01-30',
    gioiTinh: 'Nam',
    diaChi: 'Thanh Hóa',
    sdt: '0923344556'
  },
  {
    id: '14',
    cccd: '079202022222',
    hoTen: 'Vũ Thị Oanh',
    ngaySinh: '2005-03-05',
    gioiTinh: 'Nữ',
    diaChi: 'Nghệ An',
    sdt: '0934455667'
  },
  {
    id: '15',
    cccd: '079202033333',
    hoTen: 'Đinh Văn Phúc',
    ngaySinh: '2005-05-19',
    gioiTinh: 'Nam',
    diaChi: 'Bắc Ninh',
    sdt: '0945566778'
  },
  {
    id: '16',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    ngaySinh: '2005-07-12',
    gioiTinh: 'Nữ',
    diaChi: 'Bình Thuận',
    sdt: '0956677889'
  },
  {
    id: '17',
    cccd: '079202055555',
    hoTen: 'Mai Xuân Rồng',
    ngaySinh: '2005-09-08',
    gioiTinh: 'Nam',
    diaChi: 'Lâm Đồng',
    sdt: '0967788990'
  },
  {
    id: '18',
    cccd: '079202066666',
    hoTen: 'Cao Thị Sen',
    ngaySinh: '2005-11-21',
    gioiTinh: 'Nữ',
    diaChi: 'Kiên Giang',
    sdt: '0978899001'
  },
  {
    id: '19',
    cccd: '079202077777',
    hoTen: 'Tô Minh Tâm',
    ngaySinh: '2005-02-27',
    gioiTinh: 'Nam',
    diaChi: 'Tây Ninh',
    sdt: '0989900112'
  },
  {
    id: '20',
    cccd: '079202088888',
    hoTen: 'Lương Thị Uyên',
    ngaySinh: '2005-04-16',
    gioiTinh: 'Nữ',
    diaChi: 'An Giang',
    sdt: '0990011223'
  },
  {
    id: '21',
    cccd: '079202099999',
    hoTen: 'Đỗ Quang Vinh',
    ngaySinh: '2005-06-09',
    gioiTinh: 'Nam',
    diaChi: 'TP.HCM',
    sdt: '0901234001'
  },
  {
    id: '22',
    cccd: '079203011111',
    hoTen: 'Nguyễn Thị Xuân',
    ngaySinh: '2005-08-24',
    gioiTinh: 'Nữ',
    diaChi: 'Hà Nội',
    sdt: '0912345002'
  },
  {
    id: '23',
    cccd: '079203022222',
    hoTen: 'Trần Đình Yên',
    ngaySinh: '2005-10-18',
    gioiTinh: 'Nam',
    diaChi: 'Đà Nẵng',
    sdt: '0923456003'
  },
  {
    id: '24',
    cccd: '079203033333',
    hoTen: 'Lê Thị Ánh',
    ngaySinh: '2005-12-07',
    gioiTinh: 'Nữ',
    diaChi: 'Cần Thơ',
    sdt: '0934567004'
  },
  {
    id: '25',
    cccd: '079203044444',
    hoTen: 'Phạm Hữu Bách',
    ngaySinh: '2005-01-22',
    gioiTinh: 'Nam',
    diaChi: 'Hải Phòng',
    sdt: '0945678005'
  },
  {
    id: '26',
    cccd: '079203055555',
    hoTen: 'Hoàng Thị Cẩm',
    ngaySinh: '2005-03-11',
    gioiTinh: 'Nữ',
    diaChi: 'Bình Dương',
    sdt: '0956789006'
  },
  {
    id: '27',
    cccd: '079203066666',
    hoTen: 'Võ Đức Dũng',
    ngaySinh: '2005-05-28',
    gioiTinh: 'Nam',
    diaChi: 'Đồng Nai',
    sdt: '0967890007'
  },
  {
    id: '28',
    cccd: '079203077777',
    hoTen: 'Đặng Thị Giang',
    ngaySinh: '2005-07-04',
    gioiTinh: 'Nữ',
    diaChi: 'Huế',
    sdt: '0978901008'
  },
  {
    id: '29',
    cccd: '079203088888',
    hoTen: 'Bùi Thanh Hải',
    ngaySinh: '2005-09-15',
    gioiTinh: 'Nam',
    diaChi: 'Vũng Tàu',
    sdt: '0989012009'
  },
  {
    id: '30',
    cccd: '079203099999',
    hoTen: 'Ngô Thị Hương',
    ngaySinh: '2005-11-30',
    gioiTinh: 'Nữ',
    diaChi: 'Long An',
    sdt: '0990123010'
  },
  {
    id: '31',
    cccd: '079204011111',
    hoTen: 'Dương Văn Khoa',
    ngaySinh: '2005-02-06',
    gioiTinh: 'Nam',
    diaChi: 'Quảng Ninh',
    sdt: '0901234011'
  },
  {
    id: '32',
    cccd: '079204022222',
    hoTen: 'Lý Thị Lan',
    ngaySinh: '2005-04-23',
    gioiTinh: 'Nữ',
    diaChi: 'Thanh Hóa',
    sdt: '0912345012'
  },
  {
    id: '33',
    cccd: '079204033333',
    hoTen: 'Phan Quốc Minh',
    ngaySinh: '2005-06-14',
    gioiTinh: 'Nam',
    diaChi: 'Nghệ An',
    sdt: '0923456013'
  },
  {
    id: '34',
    cccd: '079204044444',
    hoTen: 'Trịnh Thị Ngọc',
    ngaySinh: '2005-08-09',
    gioiTinh: 'Nữ',
    diaChi: 'Bắc Ninh',
    sdt: '0934567014'
  },
  {
    id: '35',
    cccd: '079204055555',
    hoTen: 'Vũ Hoàng Phong',
    ngaySinh: '2005-10-26',
    gioiTinh: 'Nam',
    diaChi: 'Bình Thuận',
    sdt: '0945678015'
  },
  {
    id: '36',
    cccd: '079204066666',
    hoTen: 'Đinh Thị Quyên',
    ngaySinh: '2005-12-13',
    gioiTinh: 'Nữ',
    diaChi: 'Lâm Đồng',
    sdt: '0956789016'
  },
  {
    id: '37',
    cccd: '079204077777',
    hoTen: 'Hồ Trung Sơn',
    ngaySinh: '2005-01-08',
    gioiTinh: 'Nam',
    diaChi: 'Kiên Giang',
    sdt: '0967890017'
  },
  {
    id: '38',
    cccd: '079204088888',
    hoTen: 'Mai Thị Thảo',
    ngaySinh: '2005-03-25',
    gioiTinh: 'Nữ',
    diaChi: 'Tây Ninh',
    sdt: '0978901018'
  },
  {
    id: '39',
    cccd: '079204099999',
    hoTen: 'Cao Đức Trung',
    ngaySinh: '2005-05-02',
    gioiTinh: 'Nam',
    diaChi: 'An Giang',
    sdt: '0989012019'
  },
  {
    id: '40',
    cccd: '079205011111',
    hoTen: 'Tô Thị Vân',
    ngaySinh: '2005-07-19',
    gioiTinh: 'Nữ',
    diaChi: 'TP.HCM',
    sdt: '0990123020'
  },
  {
    id: '41',
    cccd: '079205022222',
    hoTen: 'Lương Minh Đạt',
    ngaySinh: '2005-09-06',
    gioiTinh: 'Nam',
    diaChi: 'Hà Nội',
    sdt: '0901234021'
  },
  {
    id: '42',
    cccd: '079205033333',
    hoTen: 'Đỗ Thị Hồng',
    ngaySinh: '2005-11-14',
    gioiTinh: 'Nữ',
    diaChi: 'Đà Nẵng',
    sdt: '0912345022'
  },
  {
    id: '43',
    cccd: '079205044444',
    hoTen: 'Nguyễn Công Khánh',
    ngaySinh: '2005-02-18',
    gioiTinh: 'Nam',
    diaChi: 'Cần Thơ',
    sdt: '0923456023'
  },
  {
    id: '44',
    cccd: '079205055555',
    hoTen: 'Trần Thị Linh',
    ngaySinh: '2005-04-30',
    gioiTinh: 'Nữ',
    diaChi: 'Hải Phòng',
    sdt: '0934567024'
  },
  {
    id: '45',
    cccd: '079205066666',
    hoTen: 'Lê Hoàng Nam',
    ngaySinh: '2005-06-25',
    gioiTinh: 'Nam',
    diaChi: 'Bình Dương',
    sdt: '0945678025'
  },
  {
    id: '46',
    cccd: '079205077777',
    hoTen: 'Phạm Thị Phượng',
    ngaySinh: '2005-08-11',
    gioiTinh: 'Nữ',
    diaChi: 'Đồng Nai',
    sdt: '0956789026'
  },
  {
    id: '47',
    cccd: '079205088888',
    hoTen: 'Hoàng Bá Quân',
    ngaySinh: '2005-10-07',
    gioiTinh: 'Nam',
    diaChi: 'Huế',
    sdt: '0967890027'
  },
  {
    id: '48',
    cccd: '079205099999',
    hoTen: 'Võ Thị Thu',
    ngaySinh: '2005-12-22',
    gioiTinh: 'Nữ',
    diaChi: 'Vũng Tàu',
    sdt: '0978901028'
  },
  {
    id: '49',
    cccd: '079206011111',
    hoTen: 'Đặng Anh Tuấn',
    ngaySinh: '2005-01-05',
    gioiTinh: 'Nam',
    diaChi: 'Long An',
    sdt: '0989012029'
  },
  {
    id: '50',
    cccd: '079206022222',
    hoTen: 'Bùi Thị Yến',
    ngaySinh: '2005-03-17',
    gioiTinh: 'Nữ',
    diaChi: 'Quảng Ninh',
    sdt: '0990123030'
  }]
  );
  const [majors, setMajors] = useState<Major[]>([
  {
    id: '1',
    maNganh: 'CNTT',
    tenNganh: 'Công nghệ thông tin',
    chiTieu: 200
  },
  {
    id: '2',
    maNganh: 'QTKD',
    tenNganh: 'Quản trị kinh doanh',
    chiTieu: 150
  },
  {
    id: '3',
    maNganh: 'KT',
    tenNganh: 'Kế toán',
    chiTieu: 120
  },
  {
    id: '4',
    maNganh: 'DTVT',
    tenNganh: 'Điện tử viễn thông',
    chiTieu: 100
  },
  {
    id: '5',
    maNganh: 'KTPM',
    tenNganh: 'Kỹ thuật phần mềm',
    chiTieu: 180
  },
  {
    id: '6',
    maNganh: 'HTTT',
    tenNganh: 'Hệ thống thông tin',
    chiTieu: 130
  },
  {
    id: '7',
    maNganh: 'KHMT',
    tenNganh: 'Khoa học máy tính',
    chiTieu: 160
  },
  {
    id: '8',
    maNganh: 'TMDT',
    tenNganh: 'Thương mại điện tử',
    chiTieu: 110
  },
  {
    id: '9',
    maNganh: 'TCNH',
    tenNganh: 'Tài chính ngân hàng',
    chiTieu: 140
  },
  {
    id: '10',
    maNganh: 'MARKETING',
    tenNganh: 'Marketing',
    chiTieu: 120
  },
  {
    id: '11',
    maNganh: 'LUAT',
    tenNganh: 'Luật kinh tế',
    chiTieu: 100
  },
  {
    id: '12',
    maNganh: 'NNHAT',
    tenNganh: 'Ngôn ngữ Nhật',
    chiTieu: 80
  },
  {
    id: '13',
    maNganh: 'NNANH',
    tenNganh: 'Ngôn ngữ Anh',
    chiTieu: 150
  },
  {
    id: '14',
    maNganh: 'NNHAN',
    tenNganh: 'Ngôn ngữ Hàn',
    chiTieu: 70
  },
  {
    id: '15',
    maNganh: 'NNTQ',
    tenNganh: 'Ngôn ngữ Trung Quốc',
    chiTieu: 60
  },
  {
    id: '16',
    maNganh: 'SPMN',
    tenNganh: 'Sư phạm Mầm non',
    chiTieu: 90
  },
  {
    id: '17',
    maNganh: 'SPTH',
    tenNganh: 'Sư phạm Tiểu học',
    chiTieu: 100
  },
  {
    id: '18',
    maNganh: 'SPTOAN',
    tenNganh: 'Sư phạm Toán',
    chiTieu: 80
  },
  {
    id: '19',
    maNganh: 'CNSH',
    tenNganh: 'Công nghệ sinh học',
    chiTieu: 90
  },
  {
    id: '20',
    maNganh: 'CNTP',
    tenNganh: 'Công nghệ thực phẩm',
    chiTieu: 85
  },
  {
    id: '21',
    maNganh: 'MTTN',
    tenNganh: 'Môi trường tài nguyên',
    chiTieu: 70
  },
  {
    id: '22',
    maNganh: 'XDDD',
    tenNganh: 'Xây dựng dân dụng',
    chiTieu: 110
  },
  {
    id: '23',
    maNganh: 'CKOT',
    tenNganh: 'Cơ khí ô tô',
    chiTieu: 95
  },
  {
    id: '24',
    maNganh: 'DIEN',
    tenNganh: 'Công nghệ kỹ thuật điện',
    chiTieu: 100
  },
  {
    id: '25',
    maNganh: 'DLKS',
    tenNganh: 'Du lịch - Khách sạn',
    chiTieu: 120
  }]
  );
  const [conversionRules, setConversionRules] = useState<ConversionRule[]>([
  {
    id: '1',
    khoiGoc: 'A01',
    khoiDich: 'A00',
    mucChenhLech: 0.69
  },
  {
    id: '2',
    khoiGoc: 'B00',
    khoiDich: 'A00',
    mucChenhLech: -1.21
  },
  {
    id: '3',
    khoiGoc: 'D01',
    khoiDich: 'A00',
    mucChenhLech: 0.5
  },
  {
    id: '4',
    khoiGoc: 'A02',
    khoiDich: 'A00',
    mucChenhLech: 0.35
  },
  {
    id: '5',
    khoiGoc: 'B01',
    khoiDich: 'A00',
    mucChenhLech: -0.85
  },
  {
    id: '6',
    khoiGoc: 'B03',
    khoiDich: 'A00',
    mucChenhLech: -0.92
  },
  {
    id: '7',
    khoiGoc: 'C00',
    khoiDich: 'A00',
    mucChenhLech: -1.5
  },
  {
    id: '8',
    khoiGoc: 'C01',
    khoiDich: 'A00',
    mucChenhLech: -1.35
  },
  {
    id: '9',
    khoiGoc: 'D07',
    khoiDich: 'A00',
    mucChenhLech: 0.42
  },
  {
    id: '10',
    khoiGoc: 'D08',
    khoiDich: 'A00',
    mucChenhLech: 0.38
  },
  {
    id: '11',
    khoiGoc: 'D09',
    khoiDich: 'A00',
    mucChenhLech: 0.55
  },
  {
    id: '12',
    khoiGoc: 'D10',
    khoiDich: 'A00',
    mucChenhLech: 0.28
  },
  {
    id: '13',
    khoiGoc: 'A01',
    khoiDich: 'D01',
    mucChenhLech: 0.19
  },
  {
    id: '14',
    khoiGoc: 'B00',
    khoiDich: 'D01',
    mucChenhLech: -1.71
  },
  {
    id: '15',
    khoiGoc: 'A02',
    khoiDich: 'D01',
    mucChenhLech: -0.15
  },
  {
    id: '16',
    khoiGoc: 'C00',
    khoiDich: 'D01',
    mucChenhLech: -2.0
  },
  {
    id: '17',
    khoiGoc: 'D07',
    khoiDich: 'D01',
    mucChenhLech: -0.08
  },
  {
    id: '18',
    khoiGoc: 'A00',
    khoiDich: 'B00',
    mucChenhLech: 1.21
  },
  {
    id: '19',
    khoiGoc: 'A01',
    khoiDich: 'B00',
    mucChenhLech: 1.9
  },
  {
    id: '20',
    khoiGoc: 'D01',
    khoiDich: 'B00',
    mucChenhLech: 1.71
  },
  {
    id: '21',
    khoiGoc: 'B01',
    khoiDich: 'B00',
    mucChenhLech: 0.36
  },
  {
    id: '22',
    khoiGoc: 'B03',
    khoiDich: 'B00',
    mucChenhLech: 0.29
  },
  {
    id: '23',
    khoiGoc: 'C01',
    khoiDich: 'C00',
    mucChenhLech: 0.15
  },
  {
    id: '24',
    khoiGoc: 'D01',
    khoiDich: 'C00',
    mucChenhLech: 2.0
  },
  {
    id: '25',
    khoiGoc: 'D08',
    khoiDich: 'D01',
    mucChenhLech: -0.12
  }]
  );
  const [subjectCombinations, setSubjectCombinations] = useState<
    SubjectCombination[]>(
    [
    {
      id: '1',
      maToHop: 'A00',
      tenToHop: 'Toán, Lý, Hóa',
      mon1: 'Toán',
      mon2: 'Lý',
      mon3: 'Hóa'
    },
    {
      id: '2',
      maToHop: 'A01',
      tenToHop: 'Toán, Lý, Anh',
      mon1: 'Toán',
      mon2: 'Lý',
      mon3: 'Anh'
    },
    {
      id: '3',
      maToHop: 'A02',
      tenToHop: 'Toán, Lý, Sinh',
      mon1: 'Toán',
      mon2: 'Lý',
      mon3: 'Sinh'
    },
    {
      id: '4',
      maToHop: 'B00',
      tenToHop: 'Toán, Hóa, Sinh',
      mon1: 'Toán',
      mon2: 'Hóa',
      mon3: 'Sinh'
    },
    {
      id: '5',
      maToHop: 'B01',
      tenToHop: 'Toán, Sinh, Sử',
      mon1: 'Toán',
      mon2: 'Sinh',
      mon3: 'Sử'
    },
    {
      id: '6',
      maToHop: 'B03',
      tenToHop: 'Toán, Sinh, Văn',
      mon1: 'Toán',
      mon2: 'Sinh',
      mon3: 'Văn'
    },
    {
      id: '7',
      maToHop: 'C00',
      tenToHop: 'Văn, Sử, Địa',
      mon1: 'Văn',
      mon2: 'Sử',
      mon3: 'Địa'
    },
    {
      id: '8',
      maToHop: 'C01',
      tenToHop: 'Văn, Toán, Lý',
      mon1: 'Văn',
      mon2: 'Toán',
      mon3: 'Lý'
    },
    {
      id: '9',
      maToHop: 'D01',
      tenToHop: 'Toán, Văn, Anh',
      mon1: 'Toán',
      mon2: 'Văn',
      mon3: 'Anh'
    },
    {
      id: '10',
      maToHop: 'D07',
      tenToHop: 'Toán, KHTN, Anh',
      mon1: 'Toán',
      mon2: 'KHTN',
      mon3: 'Anh'
    },
    {
      id: '11',
      maToHop: 'D08',
      tenToHop: 'Toán, Sinh, Anh',
      mon1: 'Toán',
      mon2: 'Sinh',
      mon3: 'Anh'
    },
    {
      id: '12',
      maToHop: 'D09',
      tenToHop: 'Toán, Sử, Anh',
      mon1: 'Toán',
      mon2: 'Sử',
      mon3: 'Anh'
    },
    {
      id: '13',
      maToHop: 'D10',
      tenToHop: 'Toán, Địa, Anh',
      mon1: 'Toán',
      mon2: 'Địa',
      mon3: 'Anh'
    },
    {
      id: '14',
      maToHop: 'D14',
      tenToHop: 'Văn, Sử, Anh',
      mon1: 'Văn',
      mon2: 'Sử',
      mon3: 'Anh'
    },
    {
      id: '15',
      maToHop: 'D15',
      tenToHop: 'Văn, Địa, Anh',
      mon1: 'Văn',
      mon2: 'Địa',
      mon3: 'Anh'
    },
    {
      id: '16',
      maToHop: 'D66',
      tenToHop: 'Văn, GDCD, Anh',
      mon1: 'Văn',
      mon2: 'GDCD',
      mon3: 'Anh'
    },
    {
      id: '17',
      maToHop: 'D78',
      tenToHop: 'Văn, KHXH, Anh',
      mon1: 'Văn',
      mon2: 'KHXH',
      mon3: 'Anh'
    },
    {
      id: '18',
      maToHop: 'D84',
      tenToHop: 'Toán, GDCD, Anh',
      mon1: 'Toán',
      mon2: 'GDCD',
      mon3: 'Anh'
    },
    {
      id: '19',
      maToHop: 'D90',
      tenToHop: 'Toán, KHTN, GDCD',
      mon1: 'Toán',
      mon2: 'KHTN',
      mon3: 'GDCD'
    },
    {
      id: '20',
      maToHop: 'D96',
      tenToHop: 'Toán, KHXH, Anh',
      mon1: 'Toán',
      mon2: 'KHXH',
      mon3: 'Anh'
    },
    {
      id: '21',
      maToHop: 'H00',
      tenToHop: 'Văn, Vẽ MT, Vẽ TT',
      mon1: 'Văn',
      mon2: 'Vẽ MT',
      mon3: 'Vẽ TT'
    },
    {
      id: '22',
      maToHop: 'M00',
      tenToHop: 'Văn, Toán, Anh',
      mon1: 'Văn',
      mon2: 'Toán',
      mon3: 'Anh'
    },
    {
      id: '23',
      maToHop: 'R00',
      tenToHop: 'Văn, Sử, Địa (KHXH)',
      mon1: 'Văn',
      mon2: 'Sử',
      mon3: 'Địa'
    },
    {
      id: '24',
      maToHop: 'V00',
      tenToHop: 'Toán, Lý, Vẽ MT',
      mon1: 'Toán',
      mon2: 'Lý',
      mon3: 'Vẽ MT'
    },
    {
      id: '25',
      maToHop: 'V01',
      tenToHop: 'Toán, Vẽ MT, Anh',
      mon1: 'Toán',
      mon2: 'Vẽ MT',
      mon3: 'Anh'
    }]
  );
  const [majorCombinations, setMajorCombinations] = useState<
    MajorCombination[]>(
    [
    {
      id: '1',
      maNganh: 'CNTT',
      maToHop: 'A00'
    },
    {
      id: '2',
      maNganh: 'CNTT',
      maToHop: 'A01'
    },
    {
      id: '3',
      maNganh: 'CNTT',
      maToHop: 'D01'
    },
    {
      id: '4',
      maNganh: 'QTKD',
      maToHop: 'A00'
    },
    {
      id: '5',
      maNganh: 'QTKD',
      maToHop: 'D01'
    },
    {
      id: '6',
      maNganh: 'KT',
      maToHop: 'A00'
    },
    {
      id: '7',
      maNganh: 'KT',
      maToHop: 'D01'
    },
    {
      id: '8',
      maNganh: 'DTVT',
      maToHop: 'A00'
    },
    {
      id: '9',
      maNganh: 'DTVT',
      maToHop: 'A01'
    },
    {
      id: '10',
      maNganh: 'KTPM',
      maToHop: 'A00'
    },
    {
      id: '11',
      maNganh: 'KTPM',
      maToHop: 'A01'
    },
    {
      id: '12',
      maNganh: 'KTPM',
      maToHop: 'D01'
    },
    {
      id: '13',
      maNganh: 'HTTT',
      maToHop: 'A00'
    },
    {
      id: '14',
      maNganh: 'HTTT',
      maToHop: 'A01'
    },
    {
      id: '15',
      maNganh: 'KHMT',
      maToHop: 'A00'
    },
    {
      id: '16',
      maNganh: 'KHMT',
      maToHop: 'A01'
    },
    {
      id: '17',
      maNganh: 'TMDT',
      maToHop: 'A00'
    },
    {
      id: '18',
      maNganh: 'TMDT',
      maToHop: 'D01'
    },
    {
      id: '19',
      maNganh: 'TCNH',
      maToHop: 'A00'
    },
    {
      id: '20',
      maNganh: 'TCNH',
      maToHop: 'D01'
    },
    {
      id: '21',
      maNganh: 'MARKETING',
      maToHop: 'A00'
    },
    {
      id: '22',
      maNganh: 'MARKETING',
      maToHop: 'D01'
    },
    {
      id: '23',
      maNganh: 'LUAT',
      maToHop: 'C00'
    },
    {
      id: '24',
      maNganh: 'LUAT',
      maToHop: 'D01'
    },
    {
      id: '25',
      maNganh: 'NNHAT',
      maToHop: 'D01'
    },
    {
      id: '26',
      maNganh: 'NNANH',
      maToHop: 'D01'
    },
    {
      id: '27',
      maNganh: 'NNANH',
      maToHop: 'D14'
    },
    {
      id: '28',
      maNganh: 'NNHAN',
      maToHop: 'D01'
    },
    {
      id: '29',
      maNganh: 'NNTQ',
      maToHop: 'D01'
    },
    {
      id: '30',
      maNganh: 'SPMN',
      maToHop: 'M00'
    },
    {
      id: '31',
      maNganh: 'SPMN',
      maToHop: 'D01'
    },
    {
      id: '32',
      maNganh: 'SPTH',
      maToHop: 'D01'
    },
    {
      id: '33',
      maNganh: 'SPTH',
      maToHop: 'A00'
    },
    {
      id: '34',
      maNganh: 'SPTOAN',
      maToHop: 'A00'
    },
    {
      id: '35',
      maNganh: 'SPTOAN',
      maToHop: 'A01'
    },
    {
      id: '36',
      maNganh: 'CNSH',
      maToHop: 'B00'
    },
    {
      id: '37',
      maNganh: 'CNSH',
      maToHop: 'A02'
    },
    {
      id: '38',
      maNganh: 'CNTP',
      maToHop: 'B00'
    },
    {
      id: '39',
      maNganh: 'CNTP',
      maToHop: 'A00'
    },
    {
      id: '40',
      maNganh: 'MTTN',
      maToHop: 'B00'
    },
    {
      id: '41',
      maNganh: 'MTTN',
      maToHop: 'D07'
    },
    {
      id: '42',
      maNganh: 'XDDD',
      maToHop: 'A00'
    },
    {
      id: '43',
      maNganh: 'XDDD',
      maToHop: 'A01'
    },
    {
      id: '44',
      maNganh: 'XDDD',
      maToHop: 'V00'
    },
    {
      id: '45',
      maNganh: 'CKOT',
      maToHop: 'A00'
    },
    {
      id: '46',
      maNganh: 'CKOT',
      maToHop: 'A01'
    },
    {
      id: '47',
      maNganh: 'DIEN',
      maToHop: 'A00'
    },
    {
      id: '48',
      maNganh: 'DIEN',
      maToHop: 'A01'
    },
    {
      id: '49',
      maNganh: 'DLKS',
      maToHop: 'D01'
    },
    {
      id: '50',
      maNganh: 'DLKS',
      maToHop: 'D14'
    },
    {
      id: '51',
      maNganh: 'DLKS',
      maToHop: 'D15'
    }]
  );
  const [candidateScores, setCandidateScores] = useState<CandidateScore[]>([
  // Nguyễn Văn An - THPT
  {
    id: '1',
    cccd: '079201012345',
    hoTen: 'Nguyễn Văn An',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 9.0
  },
  {
    id: '2',
    cccd: '079201012345',
    hoTen: 'Nguyễn Văn An',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 8.5
  },
  {
    id: '3',
    cccd: '079201012345',
    hoTen: 'Nguyễn Văn An',
    loaiDiem: 'THPT',
    mon: 'Hóa',
    diem: 8.0
  },
  {
    id: '4',
    cccd: '079201012345',
    hoTen: 'Nguyễn Văn An',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 7.5
  },
  // Trần Thị Bình - THPT
  {
    id: '5',
    cccd: '079201054321',
    hoTen: 'Trần Thị Bình',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 8.0
  },
  {
    id: '6',
    cccd: '079201054321',
    hoTen: 'Trần Thị Bình',
    loaiDiem: 'THPT',
    mon: 'Văn',
    diem: 7.5
  },
  {
    id: '7',
    cccd: '079201054321',
    hoTen: 'Trần Thị Bình',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 7.5
  },
  {
    id: '8',
    cccd: '079201054321',
    hoTen: 'Trần Thị Bình',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 6.5
  },
  // Lê Minh Châu - THPT
  {
    id: '9',
    cccd: '079201098765',
    hoTen: 'Lê Minh Châu',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 9.5
  },
  {
    id: '10',
    cccd: '079201098765',
    hoTen: 'Lê Minh Châu',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 9.0
  },
  {
    id: '11',
    cccd: '079201098765',
    hoTen: 'Lê Minh Châu',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 8.5
  },
  {
    id: '12',
    cccd: '079201098765',
    hoTen: 'Lê Minh Châu',
    loaiDiem: 'THPT',
    mon: 'Hóa',
    diem: 8.0
  },
  // Phạm Thị Dung - THPT + DGNL
  {
    id: '13',
    cccd: '079201011111',
    hoTen: 'Phạm Thị Dung',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 7.5
  },
  {
    id: '14',
    cccd: '079201011111',
    hoTen: 'Phạm Thị Dung',
    loaiDiem: 'THPT',
    mon: 'Văn',
    diem: 7.0
  },
  {
    id: '15',
    cccd: '079201011111',
    hoTen: 'Phạm Thị Dung',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 7.0
  },
  {
    id: '16',
    cccd: '079201011111',
    hoTen: 'Phạm Thị Dung',
    loaiDiem: 'DGNL',
    mon: 'Tổng hợp',
    diem: 850
  },
  // Hoàng Văn Em - THPT
  {
    id: '17',
    cccd: '079201022222',
    hoTen: 'Hoàng Văn Em',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 8.5
  },
  {
    id: '18',
    cccd: '079201022222',
    hoTen: 'Hoàng Văn Em',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 8.0
  },
  {
    id: '19',
    cccd: '079201022222',
    hoTen: 'Hoàng Văn Em',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 7.5
  },
  // Võ Thị Phương - THPT
  {
    id: '20',
    cccd: '079201033333',
    hoTen: 'Võ Thị Phương',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 6.5
  },
  {
    id: '21',
    cccd: '079201033333',
    hoTen: 'Võ Thị Phương',
    loaiDiem: 'THPT',
    mon: 'Văn',
    diem: 6.0
  },
  {
    id: '22',
    cccd: '079201033333',
    hoTen: 'Võ Thị Phương',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 6.0
  },
  // Đặng Quốc Bảo - THPT + VSAT
  {
    id: '23',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 9.2
  },
  {
    id: '24',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 8.8
  },
  {
    id: '25',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    loaiDiem: 'THPT',
    mon: 'Hóa',
    diem: 8.5
  },
  {
    id: '26',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    loaiDiem: 'VSAT',
    mon: 'Tư duy Toán',
    diem: 85
  },
  {
    id: '27',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    loaiDiem: 'VSAT',
    mon: 'Tư duy Đọc hiểu',
    diem: 78
  },
  // Bùi Thị Hạnh - THPT
  {
    id: '28',
    cccd: '079201055555',
    hoTen: 'Bùi Thị Hạnh',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 7.0
  },
  {
    id: '29',
    cccd: '079201055555',
    hoTen: 'Bùi Thị Hạnh',
    loaiDiem: 'THPT',
    mon: 'Hóa',
    diem: 7.5
  },
  {
    id: '30',
    cccd: '079201055555',
    hoTen: 'Bùi Thị Hạnh',
    loaiDiem: 'THPT',
    mon: 'Sinh',
    diem: 8.0
  },
  // Ngô Thanh Hùng - THPT + DGNL
  {
    id: '31',
    cccd: '079201066666',
    hoTen: 'Ngô Thanh Hùng',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 8.0
  },
  {
    id: '32',
    cccd: '079201066666',
    hoTen: 'Ngô Thanh Hùng',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 7.5
  },
  {
    id: '33',
    cccd: '079201066666',
    hoTen: 'Ngô Thanh Hùng',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 7.0
  },
  {
    id: '34',
    cccd: '079201066666',
    hoTen: 'Ngô Thanh Hùng',
    loaiDiem: 'DGNL',
    mon: 'Tổng hợp',
    diem: 780
  },
  // Dương Thị Kim - THPT
  {
    id: '35',
    cccd: '079201077777',
    hoTen: 'Dương Thị Kim',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 7.5
  },
  {
    id: '36',
    cccd: '079201077777',
    hoTen: 'Dương Thị Kim',
    loaiDiem: 'THPT',
    mon: 'Văn',
    diem: 8.0
  },
  {
    id: '37',
    cccd: '079201077777',
    hoTen: 'Dương Thị Kim',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 8.5
  },
  // Lý Văn Long - THPT
  {
    id: '38',
    cccd: '079201088888',
    hoTen: 'Lý Văn Long',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 8.8
  },
  {
    id: '39',
    cccd: '079201088888',
    hoTen: 'Lý Văn Long',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 8.2
  },
  {
    id: '40',
    cccd: '079201088888',
    hoTen: 'Lý Văn Long',
    loaiDiem: 'THPT',
    mon: 'Hóa',
    diem: 7.8
  },
  // Phan Thị Mai - THPT + VSAT
  {
    id: '41',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 6.8
  },
  {
    id: '42',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    loaiDiem: 'THPT',
    mon: 'Văn',
    diem: 7.2
  },
  {
    id: '43',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 7.0
  },
  {
    id: '44',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    loaiDiem: 'VSAT',
    mon: 'Tư duy Toán',
    diem: 72
  },
  {
    id: '45',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    loaiDiem: 'VSAT',
    mon: 'Tư duy Đọc hiểu',
    diem: 80
  },
  // Trịnh Đức Nam - THPT
  {
    id: '46',
    cccd: '079202011111',
    hoTen: 'Trịnh Đức Nam',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 9.0
  },
  {
    id: '47',
    cccd: '079202011111',
    hoTen: 'Trịnh Đức Nam',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 8.5
  },
  {
    id: '48',
    cccd: '079202011111',
    hoTen: 'Trịnh Đức Nam',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 8.0
  },
  // Vũ Thị Oanh - THPT + DGNL
  {
    id: '49',
    cccd: '079202022222',
    hoTen: 'Vũ Thị Oanh',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 7.0
  },
  {
    id: '50',
    cccd: '079202022222',
    hoTen: 'Vũ Thị Oanh',
    loaiDiem: 'THPT',
    mon: 'Văn',
    diem: 8.5
  },
  {
    id: '51',
    cccd: '079202022222',
    hoTen: 'Vũ Thị Oanh',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 7.5
  },
  {
    id: '52',
    cccd: '079202022222',
    hoTen: 'Vũ Thị Oanh',
    loaiDiem: 'DGNL',
    mon: 'Tổng hợp',
    diem: 820
  },
  // Đinh Văn Phúc - THPT
  {
    id: '53',
    cccd: '079202033333',
    hoTen: 'Đinh Văn Phúc',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 8.5
  },
  {
    id: '54',
    cccd: '079202033333',
    hoTen: 'Đinh Văn Phúc',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 7.5
  },
  {
    id: '55',
    cccd: '079202033333',
    hoTen: 'Đinh Văn Phúc',
    loaiDiem: 'THPT',
    mon: 'Hóa',
    diem: 8.0
  },
  // Hồ Thị Quỳnh - THPT + VSAT
  {
    id: '56',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 7.8
  },
  {
    id: '57',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    loaiDiem: 'THPT',
    mon: 'Văn',
    diem: 8.2
  },
  {
    id: '58',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 7.0
  },
  {
    id: '59',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    loaiDiem: 'VSAT',
    mon: 'Tư duy Toán',
    diem: 76
  },
  {
    id: '60',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    loaiDiem: 'VSAT',
    mon: 'Khoa học',
    diem: 82
  },
  // Mai Xuân Rồng - THPT
  {
    id: '61',
    cccd: '079202055555',
    hoTen: 'Mai Xuân Rồng',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 9.2
  },
  {
    id: '62',
    cccd: '079202055555',
    hoTen: 'Mai Xuân Rồng',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 8.8
  },
  {
    id: '63',
    cccd: '079202055555',
    hoTen: 'Mai Xuân Rồng',
    loaiDiem: 'THPT',
    mon: 'Hóa',
    diem: 9.0
  },
  // Cao Thị Sen - THPT + DGNL
  {
    id: '64',
    cccd: '079202066666',
    hoTen: 'Cao Thị Sen',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 6.5
  },
  {
    id: '65',
    cccd: '079202066666',
    hoTen: 'Cao Thị Sen',
    loaiDiem: 'THPT',
    mon: 'Văn',
    diem: 7.0
  },
  {
    id: '66',
    cccd: '079202066666',
    hoTen: 'Cao Thị Sen',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 6.5
  },
  {
    id: '67',
    cccd: '079202066666',
    hoTen: 'Cao Thị Sen',
    loaiDiem: 'DGNL',
    mon: 'Tổng hợp',
    diem: 720
  },
  // Tô Minh Tâm - THPT
  {
    id: '68',
    cccd: '079202077777',
    hoTen: 'Tô Minh Tâm',
    loaiDiem: 'THPT',
    mon: 'Toán',
    diem: 8.0
  },
  {
    id: '69',
    cccd: '079202077777',
    hoTen: 'Tô Minh Tâm',
    loaiDiem: 'THPT',
    mon: 'Lý',
    diem: 7.5
  },
  {
    id: '70',
    cccd: '079202077777',
    hoTen: 'Tô Minh Tâm',
    loaiDiem: 'THPT',
    mon: 'Anh',
    diem: 8.0
  }]
  );
  const [bonusPoints, setBonusPoints] = useState<BonusPoint[]>([
  {
    id: '1',
    cccd: '079201012345',
    hoTen: 'Nguyễn Văn An',
    loaiDiemCong: 'KV1',
    diem: 0.75,
    ghiChu: 'Khu vực 1'
  },
  {
    id: '2',
    cccd: '079201054321',
    hoTen: 'Trần Thị Bình',
    loaiDiemCong: 'ĐT1',
    diem: 2.0,
    ghiChu: 'Đối tượng 1'
  },
  {
    id: '3',
    cccd: '079201098765',
    hoTen: 'Lê Minh Châu',
    loaiDiemCong: 'KV2-NT',
    diem: 0.5,
    ghiChu: 'Khu vực 2 nông thôn'
  },
  {
    id: '4',
    cccd: '079201011111',
    hoTen: 'Phạm Thị Dung',
    loaiDiemCong: 'KV2',
    diem: 0.25,
    ghiChu: 'Khu vực 2'
  },
  {
    id: '5',
    cccd: '079201022222',
    hoTen: 'Hoàng Văn Em',
    loaiDiemCong: 'ĐT2',
    diem: 1.0,
    ghiChu: 'Đối tượng 2'
  },
  {
    id: '6',
    cccd: '079201033333',
    hoTen: 'Võ Thị Phương',
    loaiDiemCong: 'KV1',
    diem: 0.75,
    ghiChu: 'Khu vực 1'
  },
  {
    id: '7',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    loaiDiemCong: 'KV2-NT',
    diem: 0.5,
    ghiChu: 'Khu vực 2 nông thôn'
  },
  {
    id: '8',
    cccd: '079201055555',
    hoTen: 'Bùi Thị Hạnh',
    loaiDiemCong: 'ĐT3',
    diem: 1.0,
    ghiChu: 'Đối tượng 3'
  },
  {
    id: '9',
    cccd: '079201066666',
    hoTen: 'Ngô Thanh Hùng',
    loaiDiemCong: 'KV3',
    diem: 0.0,
    ghiChu: 'Khu vực 3 (không cộng)'
  },
  {
    id: '10',
    cccd: '079201077777',
    hoTen: 'Dương Thị Kim',
    loaiDiemCong: 'KV1',
    diem: 0.75,
    ghiChu: 'Khu vực 1'
  },
  {
    id: '11',
    cccd: '079201088888',
    hoTen: 'Lý Văn Long',
    loaiDiemCong: 'ĐT4',
    diem: 1.0,
    ghiChu: 'Đối tượng 4'
  },
  {
    id: '12',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    loaiDiemCong: 'KV2',
    diem: 0.25,
    ghiChu: 'Khu vực 2'
  },
  {
    id: '13',
    cccd: '079202011111',
    hoTen: 'Trịnh Đức Nam',
    loaiDiemCong: 'KV2-NT',
    diem: 0.5,
    ghiChu: 'Khu vực 2 nông thôn'
  },
  {
    id: '14',
    cccd: '079202022222',
    hoTen: 'Vũ Thị Oanh',
    loaiDiemCong: 'ĐT5',
    diem: 1.0,
    ghiChu: 'Đối tượng 5'
  },
  {
    id: '15',
    cccd: '079202033333',
    hoTen: 'Đinh Văn Phúc',
    loaiDiemCong: 'KV1',
    diem: 0.75,
    ghiChu: 'Khu vực 1'
  },
  {
    id: '16',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    loaiDiemCong: 'KV2',
    diem: 0.25,
    ghiChu: 'Khu vực 2'
  },
  {
    id: '17',
    cccd: '079202055555',
    hoTen: 'Mai Xuân Rồng',
    loaiDiemCong: 'ĐT6',
    diem: 1.0,
    ghiChu: 'Đối tượng 6'
  },
  {
    id: '18',
    cccd: '079202066666',
    hoTen: 'Cao Thị Sen',
    loaiDiemCong: 'KV2-NT',
    diem: 0.5,
    ghiChu: 'Khu vực 2 nông thôn'
  },
  {
    id: '19',
    cccd: '079202077777',
    hoTen: 'Tô Minh Tâm',
    loaiDiemCong: 'KV1',
    diem: 0.75,
    ghiChu: 'Khu vực 1'
  },
  {
    id: '20',
    cccd: '079202088888',
    hoTen: 'Lương Thị Uyên',
    loaiDiemCong: 'ĐT7',
    diem: 1.0,
    ghiChu: 'Đối tượng 7'
  },
  {
    id: '21',
    cccd: '079202099999',
    hoTen: 'Đỗ Quang Vinh',
    loaiDiemCong: 'KV2',
    diem: 0.25,
    ghiChu: 'Khu vực 2'
  },
  {
    id: '22',
    cccd: '079203011111',
    hoTen: 'Nguyễn Thị Xuân',
    loaiDiemCong: 'KV1',
    diem: 0.75,
    ghiChu: 'Khu vực 1'
  },
  {
    id: '23',
    cccd: '079203022222',
    hoTen: 'Trần Đình Yên',
    loaiDiemCong: 'ĐT1',
    diem: 2.0,
    ghiChu: 'Đối tượng 1'
  },
  {
    id: '24',
    cccd: '079203033333',
    hoTen: 'Lê Thị Ánh',
    loaiDiemCong: 'KV2-NT',
    diem: 0.5,
    ghiChu: 'Khu vực 2 nông thôn'
  },
  {
    id: '25',
    cccd: '079203044444',
    hoTen: 'Phạm Hữu Bách',
    loaiDiemCong: 'KV2',
    diem: 0.25,
    ghiChu: 'Khu vực 2'
  }]
  );
  const [preferences, setPreferences] = useState<Preference[]>([
  {
    id: '1',
    cccd: '079201012345',
    hoTen: 'Nguyễn Văn An',
    thuTuNV: 1,
    maNganh: 'CNTT',
    maToHop: 'A00'
  },
  {
    id: '2',
    cccd: '079201012345',
    hoTen: 'Nguyễn Văn An',
    thuTuNV: 2,
    maNganh: 'DTVT',
    maToHop: 'A01'
  },
  {
    id: '3',
    cccd: '079201054321',
    hoTen: 'Trần Thị Bình',
    thuTuNV: 1,
    maNganh: 'QTKD',
    maToHop: 'D01'
  },
  {
    id: '4',
    cccd: '079201054321',
    hoTen: 'Trần Thị Bình',
    thuTuNV: 2,
    maNganh: 'KT',
    maToHop: 'D01'
  },
  {
    id: '5',
    cccd: '079201098765',
    hoTen: 'Lê Minh Châu',
    thuTuNV: 1,
    maNganh: 'CNTT',
    maToHop: 'A01'
  },
  {
    id: '6',
    cccd: '079201098765',
    hoTen: 'Lê Minh Châu',
    thuTuNV: 2,
    maNganh: 'KTPM',
    maToHop: 'A01'
  },
  {
    id: '7',
    cccd: '079201011111',
    hoTen: 'Phạm Thị Dung',
    thuTuNV: 1,
    maNganh: 'KT',
    maToHop: 'D01'
  },
  {
    id: '8',
    cccd: '079201011111',
    hoTen: 'Phạm Thị Dung',
    thuTuNV: 2,
    maNganh: 'QTKD',
    maToHop: 'D01'
  },
  {
    id: '9',
    cccd: '079201022222',
    hoTen: 'Hoàng Văn Em',
    thuTuNV: 1,
    maNganh: 'DTVT',
    maToHop: 'A01'
  },
  {
    id: '10',
    cccd: '079201022222',
    hoTen: 'Hoàng Văn Em',
    thuTuNV: 2,
    maNganh: 'CNTT',
    maToHop: 'A01'
  },
  {
    id: '11',
    cccd: '079201033333',
    hoTen: 'Võ Thị Phương',
    thuTuNV: 1,
    maNganh: 'QTKD',
    maToHop: 'D01'
  },
  {
    id: '12',
    cccd: '079201033333',
    hoTen: 'Võ Thị Phương',
    thuTuNV: 2,
    maNganh: 'DLKS',
    maToHop: 'D01'
  },
  {
    id: '13',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    thuTuNV: 1,
    maNganh: 'KHMT',
    maToHop: 'A00'
  },
  {
    id: '14',
    cccd: '079201044444',
    hoTen: 'Đặng Quốc Bảo',
    thuTuNV: 2,
    maNganh: 'CNTT',
    maToHop: 'A00'
  },
  {
    id: '15',
    cccd: '079201055555',
    hoTen: 'Bùi Thị Hạnh',
    thuTuNV: 1,
    maNganh: 'CNSH',
    maToHop: 'B00'
  },
  {
    id: '16',
    cccd: '079201055555',
    hoTen: 'Bùi Thị Hạnh',
    thuTuNV: 2,
    maNganh: 'CNTP',
    maToHop: 'B00'
  },
  {
    id: '17',
    cccd: '079201066666',
    hoTen: 'Ngô Thanh Hùng',
    thuTuNV: 1,
    maNganh: 'DTVT',
    maToHop: 'A01'
  },
  {
    id: '18',
    cccd: '079201066666',
    hoTen: 'Ngô Thanh Hùng',
    thuTuNV: 2,
    maNganh: 'DIEN',
    maToHop: 'A01'
  },
  {
    id: '19',
    cccd: '079201077777',
    hoTen: 'Dương Thị Kim',
    thuTuNV: 1,
    maNganh: 'NNANH',
    maToHop: 'D01'
  },
  {
    id: '20',
    cccd: '079201077777',
    hoTen: 'Dương Thị Kim',
    thuTuNV: 2,
    maNganh: 'DLKS',
    maToHop: 'D01'
  },
  {
    id: '21',
    cccd: '079201088888',
    hoTen: 'Lý Văn Long',
    thuTuNV: 1,
    maNganh: 'CNTT',
    maToHop: 'A00'
  },
  {
    id: '22',
    cccd: '079201088888',
    hoTen: 'Lý Văn Long',
    thuTuNV: 2,
    maNganh: 'KTPM',
    maToHop: 'A00'
  },
  {
    id: '23',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    thuTuNV: 1,
    maNganh: 'MARKETING',
    maToHop: 'D01'
  },
  {
    id: '24',
    cccd: '079201099999',
    hoTen: 'Phan Thị Mai',
    thuTuNV: 2,
    maNganh: 'TMDT',
    maToHop: 'D01'
  },
  {
    id: '25',
    cccd: '079202011111',
    hoTen: 'Trịnh Đức Nam',
    thuTuNV: 1,
    maNganh: 'KTPM',
    maToHop: 'A01'
  },
  {
    id: '26',
    cccd: '079202011111',
    hoTen: 'Trịnh Đức Nam',
    thuTuNV: 2,
    maNganh: 'CNTT',
    maToHop: 'A01'
  },
  {
    id: '27',
    cccd: '079202022222',
    hoTen: 'Vũ Thị Oanh',
    thuTuNV: 1,
    maNganh: 'LUAT',
    maToHop: 'D01'
  },
  {
    id: '28',
    cccd: '079202022222',
    hoTen: 'Vũ Thị Oanh',
    thuTuNV: 2,
    maNganh: 'NNANH',
    maToHop: 'D01'
  },
  {
    id: '29',
    cccd: '079202033333',
    hoTen: 'Đinh Văn Phúc',
    thuTuNV: 1,
    maNganh: 'XDDD',
    maToHop: 'A00'
  },
  {
    id: '30',
    cccd: '079202033333',
    hoTen: 'Đinh Văn Phúc',
    thuTuNV: 2,
    maNganh: 'CKOT',
    maToHop: 'A00'
  },
  {
    id: '31',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    thuTuNV: 1,
    maNganh: 'TCNH',
    maToHop: 'D01'
  },
  {
    id: '32',
    cccd: '079202044444',
    hoTen: 'Hồ Thị Quỳnh',
    thuTuNV: 2,
    maNganh: 'KT',
    maToHop: 'D01'
  },
  {
    id: '33',
    cccd: '079202055555',
    hoTen: 'Mai Xuân Rồng',
    thuTuNV: 1,
    maNganh: 'CNTT',
    maToHop: 'A00'
  },
  {
    id: '34',
    cccd: '079202055555',
    hoTen: 'Mai Xuân Rồng',
    thuTuNV: 2,
    maNganh: 'KHMT',
    maToHop: 'A00'
  },
  {
    id: '35',
    cccd: '079202066666',
    hoTen: 'Cao Thị Sen',
    thuTuNV: 1,
    maNganh: 'SPTH',
    maToHop: 'D01'
  },
  {
    id: '36',
    cccd: '079202066666',
    hoTen: 'Cao Thị Sen',
    thuTuNV: 2,
    maNganh: 'SPMN',
    maToHop: 'D01'
  },
  {
    id: '37',
    cccd: '079202077777',
    hoTen: 'Tô Minh Tâm',
    thuTuNV: 1,
    maNganh: 'DIEN',
    maToHop: 'A01'
  },
  {
    id: '38',
    cccd: '079202077777',
    hoTen: 'Tô Minh Tâm',
    thuTuNV: 2,
    maNganh: 'DTVT',
    maToHop: 'A01'
  },
  {
    id: '39',
    cccd: '079202088888',
    hoTen: 'Lương Thị Uyên',
    thuTuNV: 1,
    maNganh: 'NNHAT',
    maToHop: 'D01'
  },
  {
    id: '40',
    cccd: '079202088888',
    hoTen: 'Lương Thị Uyên',
    thuTuNV: 2,
    maNganh: 'NNHAN',
    maToHop: 'D01'
  },
  {
    id: '41',
    cccd: '079202099999',
    hoTen: 'Đỗ Quang Vinh',
    thuTuNV: 1,
    maNganh: 'HTTT',
    maToHop: 'A00'
  },
  {
    id: '42',
    cccd: '079202099999',
    hoTen: 'Đỗ Quang Vinh',
    thuTuNV: 2,
    maNganh: 'TMDT',
    maToHop: 'A00'
  },
  {
    id: '43',
    cccd: '079203011111',
    hoTen: 'Nguyễn Thị Xuân',
    thuTuNV: 1,
    maNganh: 'MARKETING',
    maToHop: 'D01'
  },
  {
    id: '44',
    cccd: '079203011111',
    hoTen: 'Nguyễn Thị Xuân',
    thuTuNV: 2,
    maNganh: 'QTKD',
    maToHop: 'D01'
  },
  {
    id: '45',
    cccd: '079203022222',
    hoTen: 'Trần Đình Yên',
    thuTuNV: 1,
    maNganh: 'CKOT',
    maToHop: 'A00'
  },
  {
    id: '46',
    cccd: '079203022222',
    hoTen: 'Trần Đình Yên',
    thuTuNV: 2,
    maNganh: 'XDDD',
    maToHop: 'A00'
  },
  {
    id: '47',
    cccd: '079203033333',
    hoTen: 'Lê Thị Ánh',
    thuTuNV: 1,
    maNganh: 'CNTP',
    maToHop: 'B00'
  },
  {
    id: '48',
    cccd: '079203033333',
    hoTen: 'Lê Thị Ánh',
    thuTuNV: 2,
    maNganh: 'CNSH',
    maToHop: 'B00'
  },
  {
    id: '49',
    cccd: '079203044444',
    hoTen: 'Phạm Hữu Bách',
    thuTuNV: 1,
    maNganh: 'SPTOAN',
    maToHop: 'A00'
  },
  {
    id: '50',
    cccd: '079203044444',
    hoTen: 'Phạm Hữu Bách',
    thuTuNV: 2,
    maNganh: 'CNTT',
    maToHop: 'A00'
  }]
  );
  const [admissionResults, setAdmissionResults] = useState<AdmissionResult[]>(
    []
  );
  return (
    <AppContext.Provider
      value={{
        candidates,
        setCandidates,
        majors,
        setMajors,
        conversionRules,
        setConversionRules,
        admissionResults,
        setAdmissionResults,
        subjectCombinations,
        setSubjectCombinations,
        majorCombinations,
        setMajorCombinations,
        candidateScores,
        setCandidateScores,
        bonusPoints,
        setBonusPoints,
        preferences,
        setPreferences
      }}>
      
      {children}
    </AppContext.Provider>);

}
export function useAppContext() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useAppContext must be used within AppProvider');
  }
  return context;
}