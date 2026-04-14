import React from 'react';
import {
  LayoutDashboardIcon,
  UsersIcon,
  GraduationCapIcon,
  ClipboardListIcon,
  PlayIcon,
  BookOpenIcon,
  LinkIcon,
  FileTextIcon,
  AwardIcon } from
'lucide-react';
interface SidebarProps {
  activeScreen: string;
  onNavigate: (screen: string) => void;
}
export function Sidebar({ activeScreen, onNavigate }: SidebarProps) {
  const menuItems = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    icon: LayoutDashboardIcon
  },
  {
    id: 'candidates',
    label: 'Quản lý thí sinh',
    icon: UsersIcon
  },
  {
    id: 'majors',
    label: 'Quản lý ngành',
    icon: GraduationCapIcon
  },
  {
    id: 'subject-combinations',
    label: 'Tổ hợp môn',
    icon: BookOpenIcon
  },
  {
    id: 'major-combinations',
    label: 'Ngành - Tổ hợp',
    icon: LinkIcon
  },
  {
    id: 'candidate-scores',
    label: 'Điểm thí sinh',
    icon: FileTextIcon
  },
  {
    id: 'bonus-points',
    label: 'Điểm cộng',
    icon: AwardIcon
  },
  {
    id: 'scores',
    label: 'Bảng quy đổi',
    icon: ClipboardListIcon
  },
  {
    id: 'admission',
    label: 'Nguyện vọng & Xét tuyển',
    icon: PlayIcon
  }];

  return (
    <div className="w-64 bg-slate-800 h-screen flex flex-col">
      <div className="p-4 mb-4 shrink-0">
        <h1 className="text-white text-xl font-bold">SGU</h1>
        <p className="text-slate-400 text-sm">Hệ thống xét tuyển</p>
      </div>

      <nav className="flex-1 overflow-y-auto px-4 pb-4 space-y-1 custom-scrollbar">
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeScreen === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onNavigate(item.id)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors text-sm ${isActive ? 'bg-blue-600 text-white' : 'text-slate-300 hover:bg-slate-700 hover:text-white'}`}>
              
              <Icon size={18} />
              <span className="font-medium">{item.label}</span>
            </button>);

        })}
      </nav>

      <style>{`
        .custom-scrollbar::-webkit-scrollbar {
          width: 6px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
          background: transparent;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
          background-color: #475569;
          border-radius: 10px;
        }
      `}</style>
    </div>);

}