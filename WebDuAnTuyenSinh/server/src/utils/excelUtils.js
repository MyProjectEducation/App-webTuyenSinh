/**
 * Tiện ích hỗ trợ xử lý file Excel bằng thuật toán Smart Scan
 */

/**
 * Chuẩn hóa chuỗi tiêu đề cột:
 * - Chữ thường
 * - Khử dấu tiếng Việt
 * - Xóa tất cả khoảng trắng
 * - Bỏ các ký tự đặc biệt không cần thiết (tùy chọn)
 * @param {string} headerName Tên cột Excel cần chuẩn hóa
 * @returns {string} Tên cột đã được chuẩn hóa
 */
const normalizeHeader = (headerName) => {
    if (!headerName) return '';
    return headerName.toString()
        .toLowerCase()
        // Khử dấu tiếng Việt
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
        // Đổi chữ đ/Đ thành d
        .replace(/[đđ]/g, 'd')
        // Xóa tất cả khoảng trắng và các ký tự đặc biệt (chỉ giữ lại chữ và số)
        .replace(/[^a-z0-9]/g, '');
};

/**
 * Quét một dòng dữ liệu từ Excel và ánh xạ vào Object chuẩn dựa trên từ điển
 * @param {Object} row Dòng dữ liệu thô từ Excel (key-value pair)
 * @param {Object} dictionary Từ điển ánh xạ (Key: chuẩn hóa, Value: tên cột DB)
 * @returns {Object} Dữ liệu đã được ánh xạ chuẩn (chỉ chứa các key có trong DB)
 */
const smartMap = (row, dictionary) => {
    let cleanRow = {};
    const recognizedColumns = [];

    for (const [excelHeader, cellValue] of Object.entries(row)) {
        // Bỏ qua giá trị rỗng hoặc undefined để tránh ghi đè null vô nghĩa
        if (cellValue === undefined || cellValue === null || cellValue === '') continue;

        const normalizedHeader = normalizeHeader(excelHeader);
        const dbColumnName = dictionary[normalizedHeader];

        if (dbColumnName) {
            cleanRow[dbColumnName] = cellValue;
            recognizedColumns.push(excelHeader); // Lưu lại header gốc để log nếu cần
        }
    }

    return { cleanRow, recognizedColumns };
};

/**
 * Tách họ tên thành Họ và Tên riêng biệt
 * @param {string} fullName Chuỗi họ tên đầy đủ
 * @returns {Object} { ho, ten }
 */
const splitFullName = (fullName) => {
    if (!fullName) return { ho: '', ten: '' };
    const parts = fullName.trim().split(/\s+/);
    if (parts.length > 1) {
        const ten = parts.pop();
        const ho = parts.join(' ');
        return { ho, ten };
    }
    return { ho: '', ten: parts[0] };
};

/**
 * Chuyển đổi đầu vào ngày tháng thành chuỗi password 8 chữ số (DDMMYYYY)
 * @param {any} dateInput Đầu vào ngày tháng (Date object, string, hoặc number)
 * @returns {string|null} Chuỗi 8 chữ số hoặc null nếu không hợp lệ
 */
const formatDateToPassword = (dateInput) => {
    if (!dateInput) return null;
    
    let d;
    if (dateInput instanceof Date) {
        d = dateInput;
    } else {
        d = new Date(dateInput);
    }

    if (isNaN(d.getTime())) return null;

    const day = d.getDate().toString().padStart(2, '0');
    const month = (d.getMonth() + 1).toString().padStart(2, '0');
    const year = d.getFullYear().toString();

    return `${day}${month}${year}`;
};

module.exports = {
    normalizeHeader,
    smartMap,
    splitFullName,
    formatDateToPassword
};
