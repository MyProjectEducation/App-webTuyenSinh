import React, { useState } from 'react';
import { AppProvider } from './context/AppContext';
import { Sidebar } from './components/Sidebar';
import { Dashboard } from './pages/Dashboard';
import { CandidateManagement } from './pages/CandidateManagement';
import { MajorManagement } from './pages/MajorManagement';
import { ScoreConversion } from './pages/ScoreConversion';
import { AdmissionProcess } from './pages/AdmissionProcess';
import { SubjectCombinationManagement } from './pages/SubjectCombinationManagement';
import { MajorCombinationManagement } from './pages/MajorCombinationManagement';
import { CandidateScoreManagement } from './pages/CandidateScoreManagement';
import { BonusPointManagement } from './pages/BonusPointManagement';
export function App() {
  const [activeScreen, setActiveScreen] = useState('dashboard');
  const renderScreen = () => {
    switch (activeScreen) {
      case 'dashboard':
        return <Dashboard onNavigate={setActiveScreen} />;
      case 'candidates':
        return <CandidateManagement />;
      case 'majors':
        return <MajorManagement />;
      case 'subject-combinations':
        return <SubjectCombinationManagement />;
      case 'major-combinations':
        return <MajorCombinationManagement />;
      case 'candidate-scores':
        return <CandidateScoreManagement />;
      case 'bonus-points':
        return <BonusPointManagement />;
      case 'scores':
        return <ScoreConversion />;
      case 'admission':
        return <AdmissionProcess />;
      default:
        return <Dashboard onNavigate={setActiveScreen} />;
    }
  };
  return (
    <AppProvider>
      <div className="flex h-screen bg-slate-100 overflow-hidden">
        <Sidebar activeScreen={activeScreen} onNavigate={setActiveScreen} />
        <main className="flex-1 overflow-y-auto">{renderScreen()}</main>
      </div>
    </AppProvider>);

}