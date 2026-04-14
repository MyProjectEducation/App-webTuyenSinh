import React, { useState } from 'react';
import { XIcon, UploadIcon } from 'lucide-react';
interface ImportModalProps {
  isOpen: boolean;
  onClose: () => void;
  onImport: (data: any[]) => void;
  columns: string[];
  title: string;
}
export function ImportModal({
  isOpen,
  onClose,
  onImport,
  columns,
  title
}: ImportModalProps) {
  const [csvText, setCsvText] = useState('');
  const [previewData, setPreviewData] = useState<any[]>([]);
  if (!isOpen) return null;
  const handleParse = () => {
    if (!csvText.trim()) return;
    const lines = csvText.split('\n').filter((line) => line.trim());
    const parsed = lines.map((line) => {
      const values = line.split(',').map((val) => val.trim());
      const obj: any = {};
      columns.forEach((col, index) => {
        obj[col] = values[index] || '';
      });
      return obj;
    });
    setPreviewData(parsed);
  };
  const handleImport = () => {
    if (previewData.length > 0) {
      onImport(previewData);
      setCsvText('');
      setPreviewData([]);
      onClose();
    }
  };
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-3xl mx-4 max-h-[90vh] flex flex-col">
        <div className="p-6 border-b border-slate-200 flex justify-between items-center">
          <h2 className="text-xl font-bold text-slate-800">{title}</h2>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600">
            
            <XIcon size={24} />
          </button>
        </div>

        <div className="p-6 overflow-y-auto flex-1">
          <div className="mb-6">
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Dán dữ liệu CSV vào đây (cách nhau bằng dấu phẩy)
            </label>
            <p className="text-xs text-slate-500 mb-2">
              Thứ tự cột: {columns.join(', ')}
            </p>
            <textarea
              className="w-full h-32 px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
              placeholder="VD: Giá trị 1, Giá trị 2, Giá trị 3..."
              value={csvText}
              onChange={(e) => setCsvText(e.target.value)} />
            
            <button
              onClick={handleParse}
              className="mt-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-sm font-medium transition-colors">
              
              Xem trước dữ liệu
            </button>
          </div>

          {previewData.length > 0 &&
          <div>
              <h3 className="text-sm font-medium text-slate-700 mb-2">
                Xem trước ({previewData.length} dòng)
              </h3>
              <div className="overflow-x-auto border border-slate-200 rounded-lg">
                <table className="w-full text-sm">
                  <thead className="bg-slate-50 border-b border-slate-200">
                    <tr>
                      {columns.map((col) =>
                    <th
                      key={col}
                      className="px-4 py-2 text-left font-medium text-slate-600">
                      
                          {col}
                        </th>
                    )}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200">
                    {previewData.slice(0, 5).map((row, i) =>
                  <tr key={i}>
                        {columns.map((col) =>
                    <td key={col} className="px-4 py-2 text-slate-800">
                            {row[col]}
                          </td>
                    )}
                      </tr>
                  )}
                  </tbody>
                </table>
              </div>
              {previewData.length > 5 &&
            <p className="text-xs text-slate-500 mt-2 text-center">
                  ... và {previewData.length - 5} dòng khác
                </p>
            }
            </div>
          }
        </div>

        <div className="p-6 border-t border-slate-200 flex justify-end gap-3">
          <button
            onClick={onClose}
            className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 transition-colors">
            
            Hủy
          </button>
          <button
            onClick={handleImport}
            disabled={previewData.length === 0}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white rounded-lg transition-colors">
            
            <UploadIcon size={18} />
            Nhập dữ liệu
          </button>
        </div>
      </div>
    </div>);

}