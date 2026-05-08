// utils/vsatConversion.js

// 1. Từ điển chuẩn hóa tên môn thi (Chống sai chính tả từ file Excel)
const SUBJECT_MAP = {
    'TO': 'Toán', 'Toan': 'Toán', 'Toán': 'Toán',
    'LI': 'Vật lí', 'Ly': 'Vật lí', 'Vật lý': 'Vật lí', 'Vật lí': 'Vật lí',
    'HO': 'Hóa học', 'Hoa': 'Hóa học', 'Hóa': 'Hóa học', 'Hóa học': 'Hóa học',
    'VA': 'Ngữ văn', 'Van': 'Ngữ văn', 'Văn': 'Ngữ văn', 'Ngữ văn': 'Ngữ văn',
    'N1_THI': 'Tiếng Anh', 'Anh': 'Tiếng Anh', 'Tiếng Anh': 'Tiếng Anh',
    'SI': 'Sinh học', 'Sinh': 'Sinh học', 'Sinh học': 'Sinh học',
    'SU': 'Lịch sử', 'Su': 'Lịch sử', 'Lịch sử': 'Lịch sử',
    'DI': 'Địa lí', 'Dia': 'Địa lí', 'Địa lý': 'Địa lí', 'Địa lí': 'Địa lí',
    'TI': 'Tin học', 'Tin': 'Tin học', 'Tin học': 'Tin học',
    'GDCD': 'GDCD', 'GD': 'GDCD', 'Giáo dục công dân': 'GDCD'
};

// 2. Ma trận bách phân vị (Lấy từ bảng của Bộ GD&ĐT)
// Lưu ý: Các số liệu này là tham khảo, cần được cập nhật theo bảng quy đổi chính thức của từng năm.
const VSAT_MATRIX = {
    'Toán': [
        { rank: '3%', a: 132.0, b: 150.0, c: 8.5, d: 10.0 },
        { rank: '5%', a: 128.5, b: 132.0, c: 8.1, d: 8.5 },
        { rank: '10%', a: 122.5, b: 128.5, c: 7.75, d: 8.1 },
        { rank: '20%', a: 114.5, b: 122.5, c: 7.0, d: 7.75 },
        { rank: '30%', a: 108.0, b: 114.5, c: 6.6, d: 7.0 },
        { rank: '40%', a: 100.0, b: 108.0, c: 6.0, d: 6.6 },
        { rank: '50%', a: 92.0, b: 100.0, c: 5.5, d: 6.0 },
        { rank: '60%', a: 85.0, b: 92.0, c: 5.0, d: 5.5 },
        { rank: '70%', a: 75.0, b: 85.0, c: 4.5, d: 5.0 },
        { rank: '80%', a: 60.0, b: 75.0, c: 3.5, d: 4.5 },
        { rank: '90%', a: 45.0, b: 60.0, c: 2.0, d: 3.5 },
        { rank: '>90%', a: 0.0, b: 45.0, c: 0.0, d: 2.0 }
    ],
    'Vật lí': [
        { rank: '3%', a: 130.0, b: 150.0, c: 8.5, d: 10.0 },
        { rank: '5%', a: 125.0, b: 130.0, c: 8.1, d: 8.5 },
        { rank: '10%', a: 118.0, b: 125.0, c: 7.75, d: 8.1 },
        { rank: '20%', a: 110.0, b: 118.0, c: 7.0, d: 7.75 },
        { rank: '30%', a: 102.0, b: 110.0, c: 6.6, d: 7.0 },
        { rank: '40%', a: 95.0, b: 102.0, c: 6.0, d: 6.6 },
        { rank: '50%', a: 88.0, b: 95.0, c: 5.5, d: 6.0 },
        { rank: '60%', a: 80.0, b: 88.0, c: 5.0, d: 5.5 },
        { rank: '70%', a: 70.0, b: 80.0, c: 4.5, d: 5.0 },
        { rank: '80%', a: 55.0, b: 70.0, c: 3.5, d: 4.5 },
        { rank: '90%', a: 40.0, b: 55.0, c: 2.0, d: 3.5 },
        { rank: '>90%', a: 0.0, b: 40.0, c: 0.0, d: 2.0 }
    ],
    'Ngữ văn': [
        { rank: '3%', a: 125.0, b: 150.0, c: 8.5, d: 10.0 },
        { rank: '5%', a: 120.0, b: 125.0, c: 8.1, d: 8.5 },
        { rank: '10%', a: 115.0, b: 120.0, c: 7.75, d: 8.1 },
        { rank: '20%', a: 108.0, b: 115.0, c: 7.0, d: 7.75 },
        { rank: '30%', a: 100.0, b: 108.0, c: 6.6, d: 7.0 },
        { rank: '40%', a: 92.0, b: 100.0, c: 6.0, d: 6.6 },
        { rank: '50%', a: 85.0, b: 92.0, c: 5.5, d: 6.0 },
        { rank: '60%', a: 78.0, b: 85.0, c: 5.0, d: 5.5 },
        { rank: '70%', a: 68.0, b: 78.0, c: 4.5, d: 5.0 },
        { rank: '80%', a: 55.0, b: 68.0, c: 3.5, d: 4.5 },
        { rank: '90%', a: 40.0, b: 55.0, c: 2.0, d: 3.5 },
        { rank: '>90%', a: 0.0, b: 40.0, c: 0.0, d: 2.0 }
    ],
    'Tiếng Anh': [
        { rank: '3%', a: 135.0, b: 150.0, c: 8.5, d: 10.0 },
        { rank: '5%', a: 130.0, b: 135.0, c: 8.1, d: 8.5 },
        { rank: '10%', a: 124.0, b: 130.0, c: 7.75, d: 8.1 },
        { rank: '20%', a: 116.0, b: 124.0, c: 7.0, d: 7.75 },
        { rank: '30%', a: 108.0, b: 116.0, c: 6.6, d: 7.0 },
        { rank: '40%', a: 100.0, b: 108.0, c: 6.0, d: 6.6 },
        { rank: '50%', a: 90.0, b: 100.0, c: 5.5, d: 6.0 },
        { rank: '60%', a: 80.0, b: 90.0, c: 5.0, d: 5.5 },
        { rank: '70%', a: 70.0, b: 80.0, c: 4.5, d: 5.0 },
        { rank: '80%', a: 55.0, b: 70.0, c: 3.5, d: 4.5 },
        { rank: '90%', a: 40.0, b: 55.0, c: 2.0, d: 3.5 },
        { rank: '>90%', a: 0.0, b: 40.0, c: 0.0, d: 2.0 }
    ]
};

// Các môn chưa được định nghĩa chi tiết thì tạm dùng chung ma trận chuẩn của Toán để tránh lỗi
const DEFAULT_MATRIX = VSAT_MATRIX['Toán'];

/**
 * Chuyển đổi điểm V-SAT sang thang THPT 10 điểm
 * @param {string} monHocAlias - Tên môn hoặc mã môn (VD: 'TO', 'Toán')
 * @param {number} diemVsat - Điểm thi V-SAT (0 - 150)
 * @returns {number} - Điểm THPT đã làm tròn 2 chữ số thập phân
 */
exports.convertVsatToThpt = (monHocAlias, diemVsat) => {
    // 1. Chuẩn hóa tên môn
    const standardName = SUBJECT_MAP[monHocAlias];
    if (!standardName) {
        throw new Error(`[VSAT_ERROR] Môn học "${monHocAlias}" không hợp lệ để nội suy.`);
    }

    // 2. Lấy ma trận bách phân vị
    const matrix = VSAT_MATRIX[standardName] || DEFAULT_MATRIX;
    if (!matrix) {
        throw new Error(`[VSAT_ERROR] Chưa cấu hình Ma trận mốc điểm cho môn "${standardName}".`);
    }

    // 3. Xử lý điểm ngoại lệ (Out of bounds)
    const score = Number(diemVsat);
    if (isNaN(score) || score < 0) return 0;
    if (score > 150) throw new Error(`[VSAT_ERROR] Điểm V-SAT (${score}) vượt quá 150.`);

    // 4. Thuật toán quét tìm phân vị
    // Lấy range có: a <= score <= b. 
    // Tuy nhiên theo đề bài user: score > r.a && score <= r.b (Lưu ý: mốc điểm thấp nhất có thể bằng a)
    let range = matrix.find(r => score > r.a && score <= r.b);

    // Xử lý case điểm tuyệt đối (VD: score = 0 hoặc rơi vào mốc đặc biệt)
    if (!range) {
        if (score === 0) return 0; // Tránh rớt đài nếu điểm = 0
        // Lấy mốc chót cùng (ví dụ >90%)
        const lowestRange = matrix[matrix.length - 1];
        if (score <= lowestRange.a) return lowestRange.c; 
        throw new Error(`[VSAT_ERROR] Không tìm thấy phân vị phù hợp cho điểm ${score}.`);
    }

    const { a, b, c, d } = range;
    
    // Đề phòng lỗi chia cho 0 (ZeroDivisionError)
    if (a === b) return c;

    // 5. Áp dụng công thức nội suy tuyến tính: y = c + ((x - a) / (b - a)) * (d - c)
    const diemThpt = c + ((score - a) / (b - a)) * (d - c);

    // 6. Làm tròn 2 chữ số thập phân chuẩn Bộ GD&ĐT
    return Math.round(diemThpt * 100) / 100;
};

// Export thêm các hằng số để dùng trong test hoặc giao diện nếu cần
exports.SUBJECT_MAP = SUBJECT_MAP;
exports.VSAT_MATRIX = VSAT_MATRIX;
