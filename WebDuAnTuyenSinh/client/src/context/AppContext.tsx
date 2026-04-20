import React, { useState, createContext, useContext, ReactNode, useEffect } from 'react';
import { majorService } from '../services/majorService';
import { subjectComboService } from '../services/subjectComboService';
import { majorComboService } from '../services/majorComboService';
import { conversionService } from '../services/conversionService';
import { scoreService } from '../services/scoreService';
import { bonusService } from '../services/bonusService';
import { admissionService } from '../services/admissionService';

export interface Candidate {
  id: string;
  cccd: string;
  soBaoDanh: string;
  ho: string;
  ten: string;
  ngaySinh: string;
  dienThoai: string;
  password?: string;
  gioiTinh: string;
  email: string;
  noiSinh: string;
  doiTuong: string;
  khuVuc: string;
}
export interface Major {
  id: string;
  maNganh: string;
  tenNganh: string;
  toHopGoc: string;
  chiTieu: number;
  diemSan: number;
  diemTrungTuyen: number;
  tuyenThang: string; // '1' hoặc '0'
  dgnl: string;
  thpt: string;
  vsat: string;
  slXtt: number;
  slDgnl: number;
  slThpt: number; // Trong DB có thể là chuỗi nhưng frontend sẽ bind as string/number
  slVsat: number;
}
export interface ConversionRule {
  id: string;
  d_phuongthuc: string;
  d_maquydoi: string;
  d_mon: string;
  d_diema: number; // Điểm a gốc hoặc móc dưới V-SAT
  d_diemb?: number; // Điểm b quy đổi hoặc móc trên V-SAT
  d_diemc?: number; // Móc dưới THPT
  d_diemd?: number; // Móc trên THPT
  d_phanvi?: string; // Tỷ lệ phân vị
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
  thMon1?: string;
  thMon2?: string;
  thMon3?: string;
  hsMon1?: number;
  hsMon2?: number;
  hsMon3?: number;
  doLech?: number;
}
export interface CandidateScore {
  id: string;
  cccd: string;
  hoTen: string;
  loaiDiem: 'THPT' | 'VSAT' | 'DGNL';
  mon: string;
  colName?: string;
  diem: number;
}
export interface BonusPoint {
  id: string;
  cccd: string;
  hoTen: string;
  maNganh?: string;
  maToHop?: string;
  phuongThuc?: string;
  loaiDiemCong: string;
  diem: number;
  diemC?: number;
  diemUt?: number;
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
  isLoading: boolean;
  setIsLoading: (loading: boolean) => void;
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

export function AppProvider({ children }: { children: ReactNode }) {
  const [isLoading, setIsLoading] = useState(false);
  const [candidates, setCandidates] = useState<Candidate[]>([]);
  const [majors, setMajors] = useState<Major[]>([]);
  const [conversionRules, setConversionRules] = useState<ConversionRule[]>([]);
  const [admissionResults, setAdmissionResults] = useState<AdmissionResult[]>([]);
  const [subjectCombinations, setSubjectCombinations] = useState<SubjectCombination[]>([]);
  const [majorCombinations, setMajorCombinations] = useState<MajorCombination[]>([]);
  const [candidateScores, setCandidateScores] = useState<CandidateScore[]>([]);
  const [bonusPoints, setBonusPoints] = useState<BonusPoint[]>([]);
  const [preferences, setPreferences] = useState<Preference[]>([]);

  useEffect(() => {
    // Tránh fetch tự động báo lỗi 401 do chưa login
    // Ở bài toán thực tế, ta có thể chỉ gọi fetch khi đã kiểm tra có Token
    const token = localStorage.getItem('tuyensinh_token');
    if (!token) return;

    let isMounted = true;
    const loadPhase1Data = async () => {
      setIsLoading(true);
      try {
        const [majorsData, subComboData, majorComboData, convData, scoresData, bonusData, admissionData] = await Promise.all([
          majorService.getAll(),
          subjectComboService.getAll(),
          majorComboService.getAll(),
          conversionService.getAll(),
          scoreService.getAll(),
          bonusService.getAll(),
          admissionService.getAll()
        ]);
        if (isMounted) {
            setMajors(majorsData);
            setSubjectCombinations(subComboData);
            setMajorCombinations(majorComboData);
            setConversionRules(convData);
            setCandidateScores(scoresData);
            setBonusPoints(bonusData);
            setPreferences(admissionData);
        }
      } catch (err) {
        console.error("Lỗi khi fetch Phase 1 Data:", err);
      } finally {
        if (isMounted) setIsLoading(false);
      }
    };

    loadPhase1Data();
    return () => { isMounted = false; };
  }, []);

  return (
    <AppContext.Provider
      value={{
        isLoading,
        setIsLoading,
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
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useAppContext() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useAppContext must be used within AppProvider');
  }
  return context;
}