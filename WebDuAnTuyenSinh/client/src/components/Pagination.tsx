import React from 'react';

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  itemsPerPage: number;
  onPageChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  totalItems,
  itemsPerPage,
  onPageChange
}) => {
  if (totalItems === 0) return null;

  const safeTotalPages = Math.max(1, totalPages);
  const showNav = safeTotalPages > 1;

  return (
    <div className="flex items-center justify-between px-4 py-3 bg-white border-t border-slate-200 sm:px-6 mt-4 rounded-b-2xl">
      <div className="flex flex-1 flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm text-slate-700">
            Hiển thị từ <span className="font-semibold text-blue-700">{(currentPage - 1) * itemsPerPage + 1}</span>{' '}
            đến <span className="font-semibold text-blue-700">{Math.min(currentPage * itemsPerPage, totalItems)}</span>{' '}
            trong tổng số <span className="font-semibold text-blue-700">{totalItems}</span> kết quả
          </p>
        </div>

        {showNav ? (
          <div>
            <nav className="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
              <button
                type="button"
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 1}
                className={`relative inline-flex items-center rounded-l-md px-4 py-2 text-sm font-semibold ring-1 ring-inset ring-slate-300 focus:z-20 focus:outline-offset-0 ${
                  currentPage === 1 ? 'text-slate-300 bg-slate-50 cursor-not-allowed' : 'text-slate-700 bg-white hover:bg-slate-50'
                }`}
              >
                Trang trước
              </button>

              <span className="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-blue-600 bg-blue-50 ring-1 ring-inset ring-blue-600 focus:z-20 focus:outline-offset-0">
                {currentPage} / {safeTotalPages}
              </span>

              <button
                type="button"
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage === safeTotalPages}
                className={`relative inline-flex items-center rounded-r-md px-4 py-2 text-sm font-semibold ring-1 ring-inset ring-slate-300 focus:z-20 focus:outline-offset-0 ${
                  currentPage === safeTotalPages ? 'text-slate-300 bg-slate-50 cursor-not-allowed' : 'text-slate-700 bg-white hover:bg-slate-50'
                }`}
              >
                Trang sau
              </button>
            </nav>
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default Pagination;
