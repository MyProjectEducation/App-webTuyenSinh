import React from 'react';
import { useAppContext } from '../context/AppContext';
import {
  UsersIcon,
  GraduationCapIcon,
  FileTextIcon,
  BookOpenIcon,
  AwardIcon,
  ClipboardListIcon } from
'lucide-react';
interface DashboardProps {
  onNavigate: (screen: string) => void;
}
export function Dashboard({ onNavigate }: DashboardProps) {
  const {
    candidates,
    majors,
    subjectCombinations,
    candidateScores,
    preferences
  } = useAppContext();
  const stats = [
  {
    label: 'Tổng thí sinh',
    value: candidates.length,
    icon: UsersIcon,
    color: 'bg-blue-500'
  },
  {
    label: 'Tổng ngành',
    value: majors.length,
    icon: GraduationCapIcon,
    color: 'bg-green-500'
  },
  {
    label: 'Tổng tổ hợp môn',
    value: subjectCombinations.length,
    icon: BookOpenIcon,
    color: 'bg-indigo-500'
  },
  {
    label: 'Tổng nguyện vọng',
    value: preferences.length,
    icon: FileTextIcon,
    color: 'bg-purple-500'
  },
  {
    label: 'Đầu điểm đã nhập',
    value: candidateScores.length,
    icon: AwardIcon,
    color: 'bg-amber-500'
  }];

  const quickActions = [
  {
    label: 'Quản lý thí sinh',
    screen: 'candidates',
    color: 'bg-blue-600 hover:bg-blue-700'
  },
  {
    label: 'Quản lý ngành',
    screen: 'majors',
    color: 'bg-green-600 hover:bg-green-700'
  },
  {
    label: 'Nhập điểm',
    screen: 'candidate-scores',
    color: 'bg-amber-600 hover:bg-amber-700'
  },
  {
    label: 'Chạy xét tuyển',
    screen: 'admission',
    color: 'bg-orange-600 hover:bg-orange-700'
  }];

  return (
    <div className="p-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">
          HỆ THỐNG XÉT TUYỂN SGU
        </h1>
        <p className="text-slate-600">Tổng quan hệ thống quản lý tuyển sinh</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4 mb-8">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div
              key={stat.label}
              className="bg-white rounded-lg shadow-sm p-5 border border-slate-200">
              
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-slate-500 text-xs font-medium mb-1 uppercase tracking-wider">
                    {stat.label}
                  </p>
                  <p className="text-2xl font-bold text-slate-800">
                    {stat.value}
                  </p>
                </div>
                <div className={`${stat.color} p-2.5 rounded-lg`}>
                  <Icon className="text-white" size={20} />
                </div>
              </div>
            </div>);

        })}
      </div>

      <div className="bg-white rounded-lg shadow-md p-6 border border-slate-200">
        <h2 className="text-xl font-bold text-slate-800 mb-4">
          Thao tác nhanh
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {quickActions.map((action) =>
          <button
            key={action.screen}
            onClick={() => onNavigate(action.screen)}
            className={`${action.color} text-white px-6 py-4 rounded-lg font-medium transition-colors shadow-sm`}>
            
              {action.label}
            </button>
          )}
        </div>
      </div>
    </div>);

}