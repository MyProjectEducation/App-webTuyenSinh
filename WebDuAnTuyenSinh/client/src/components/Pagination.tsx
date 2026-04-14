import React from 'react';
import { ChevronLeftIcon, ChevronRightIcon } from 'lucide-react';
interface PaginationProps {
  currentPage: number;
  totalItems: number;
  itemsPerPage?: number;
  onPageChange: (page: number) => void;
}
export function Pagination({
  currentPage,
  totalItems,
  itemsPerPage = 20,
  onPageChange
}: PaginationProps) {
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  if (totalPages <= 1) return null;
  const startItem = (currentPage - 1) * itemsPerPage + 1;
  const endItem = Math.min(currentPage * itemsPerPage, totalItems);
  // Generate page numbers to show (max 5)
  let pages = [];
  if (totalPages <= 5) {
    for (let i = 1; i <= totalPages; i++) pages.push(i);
  } else {
    if (currentPage <= 3) {
      pages = [1, 2, 3, 4, 5];
    } else if (currentPage >= totalPages - 2) {
      pages = [
      totalPages - 4,
      totalPages - 3,
      totalPages - 2,
      totalPages - 1,
      totalPages];

    } else {
      pages = [
      currentPage - 2,
      currentPage - 1,
      currentPage,
      currentPage + 1,
      currentPage + 2];

    }
  }
  return (
    <div className="flex items-center justify-between px-6 py-3 border-t border-slate-200 bg-white">
      <div className="text-sm text-slate-600">
        Hiển thị{' '}
        <span className="font-medium text-slate-800">
          {startItem}-{endItem}
        </span>{' '}
        / <span className="font-medium text-slate-800">{totalItems}</span> kết
        quả
      </div>

      <div className="flex items-center gap-1">
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="p-1 rounded text-slate-500 hover:bg-slate-100 disabled:opacity-50 disabled:hover:bg-transparent">
          
          <ChevronLeftIcon size={20} />
        </button>

        {pages.map((page) =>
        <button
          key={page}
          onClick={() => onPageChange(page)}
          className={`w-8 h-8 rounded text-sm font-medium transition-colors ${currentPage === page ? 'bg-blue-600 text-white' : 'text-slate-600 hover:bg-slate-100'}`}>
          
            {page}
          </button>
        )}

        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="p-1 rounded text-slate-500 hover:bg-slate-100 disabled:opacity-50 disabled:hover:bg-transparent">
          
          <ChevronRightIcon size={20} />
        </button>
      </div>
    </div>);

}