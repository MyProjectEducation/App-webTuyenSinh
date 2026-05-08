import React from 'react';

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  itemsPerPage: number;
  onPageChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({ 
  currentPage, totalPages, totalItems, itemsPerPage, onPageChange 
}) => {
  if (totalPages <= 1) return null; // Ẩn phân trang nếu chỉ có 1 trang

  return (
    <div className="flex items-center justify-between px-4 py-3 bg-white border-t border-slate-200 sm:px-6 mt-4 rounded-b-2xl">
      <div className="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
        {/* Hiển thị tóm tắt thông tin */}
        <div>
          <p className="text-sm text-slate-700">
            Hiển thị từ <span className="font-semibold text-blue-700">{(currentPage - 1) * itemsPerPage + 1}</span>{' '}
            đến <span className="font-semibold text-blue-700">{Math.min(currentPage * itemsPerPage, totalItems)}</span>{' '}
            trong tổng số <span className="font-semibold text-blue-700">{totalItems}</span> kết quả
          </p>
        </div>
        
        {/* Nút điều hướng */}
        <div>
          <nav className="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
            <button
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage === 1}
              className={`relative inline-flex items-center rounded-l-md px-4 py-2 text-sm font-semibold ring-1 ring-inset ring-slate-300 focus:z-20 focus:outline-offset-0 ${
                currentPage === 1 ? 'text-slate-300 bg-slate-50 cursor-not-allowed' : 'text-slate-700 bg-white hover:bg-slate-50'
              }`}
            >
              Trang trước
            </button>
            
            {/* Vùng hiển thị số trang */}
            <span className="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-blue-600 bg-blue-50 ring-1 ring-inset ring-blue-600 focus:z-20 focus:outline-offset-0">
              {currentPage} / {totalPages}
            </span>

            <button
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
              className={`relative inline-flex items-center rounded-r-md px-4 py-2 text-sm font-semibold ring-1 ring-inset ring-slate-300 focus:z-20 focus:outline-offset-0 ${
                currentPage === totalPages ? 'text-slate-300 bg-slate-50 cursor-not-allowed' : 'text-slate-700 bg-white hover:bg-slate-50'
              }`}
            >
              Trang sau
            </button>
          </nav>
        </div>
      </div>
    </div>
  );
};

export default Pagination;