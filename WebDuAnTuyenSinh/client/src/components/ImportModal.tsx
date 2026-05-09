import React, { useState, useRef } from 'react';
import { XIcon, UploadIcon, FileSpreadsheetIcon, DownloadIcon } from 'lucide-react';

interface ImportModalProps {
  isOpen: boolean;
  onClose: () => void;
  onImport: (file: File) => void;
  title: string;
  onDownloadTemplate?: () => void;
}

export function ImportModal({
  isOpen,
  onClose,
  onImport,
  title,
  onDownloadTemplate
}: ImportModalProps) {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  if (!isOpen) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const handleImport = () => {
    if (selectedFile) {
      onImport(selectedFile);
      setSelectedFile(null);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  const handleClose = () => {
    setSelectedFile(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-lg mx-4 flex flex-col">
        <div className="p-6 border-b border-slate-200 flex justify-between items-center">
          <h2 className="text-xl font-bold text-slate-800">{title}</h2>
          <button
            onClick={handleClose}
            className="text-slate-400 hover:text-slate-600">
            <XIcon size={24} />
          </button>
        </div>

        <div className="p-6 flex-1">
          <div className="mb-6 flex flex-col items-center justify-center border-2 border-dashed border-slate-300 rounded-lg p-8 bg-slate-50">
            <FileSpreadsheetIcon size={48} className="text-blue-500 mb-4" />
            <p className="text-sm text-slate-600 text-center mb-4">
              {selectedFile ? (
                <span className="font-semibold text-slate-800">{selectedFile.name}</span>
              ) : (
                "Kéo thả file hoặc click để chọn file Excel (.xlsx, .xls)"
              )}
            </p>
            <input
              type="file"
              accept=".xlsx, .xls"
              className="hidden"
              ref={fileInputRef}
              onChange={handleFileChange}
            />
            <button
              onClick={() => fileInputRef.current?.click()}
              className="px-4 py-2 bg-white border border-slate-300 text-slate-700 rounded-lg text-sm font-medium hover:bg-slate-50 transition-colors"
            >
              Chọn file từ máy tính
            </button>
          </div>

          {onDownloadTemplate && (
            <div className="flex justify-center">
              <button
                onClick={onDownloadTemplate}
                className="flex items-center gap-2 text-sm text-blue-600 hover:text-blue-700 font-medium"
              >
                <DownloadIcon size={16} />
                Tải file mẫu (Template)
              </button>
            </div>
          )}
        </div>

        <div className="p-6 border-t border-slate-200 flex justify-end gap-3">
          <button
            onClick={handleClose}
            className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 transition-colors">
            Hủy
          </button>
          <button
            onClick={handleImport}
            disabled={!selectedFile}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white rounded-lg transition-colors">
            <UploadIcon size={18} />
            Nhập dữ liệu
          </button>
        </div>
      </div>
    </div>
  );
}