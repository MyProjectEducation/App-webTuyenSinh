interface VsatRange {
    rank: string;
    a: number; // Mốc dưới V-SAT
    b: number; // Mốc trên V-SAT
    c: number; // Mốc dưới THPT
    d: number; // Mốc trên THPT
}

// Bảng Mapping Tên Môn Alias
const SUBJECT_MAP: Record<string, string> = {
    'TO': 'Toán', 'Toan': 'Toán', 'Toán': 'Toán',
    'LI': 'Vật lí', 'Ly': 'Vật lí', 'Vật lý': 'Vật lí', 'Vật lí': 'Vật lí',
    'HO': 'Hóa học', 'Hoa': 'Hóa học', 'Hóa': 'Hóa học', 'Hóa học': 'Hóa học',
    'SI': 'Sinh học', 'Sinh': 'Sinh học', 'Sinh học': 'Sinh học',
    'SU': 'Lịch sử', 'Su': 'Lịch sử', 'Sử': 'Lịch sử', 'Lịch sử': 'Lịch sử',
    'DI': 'Địa lí', 'Dia': 'Địa lí', 'Địa': 'Địa lí', 'Địa lý': 'Địa lí', 'Địa lí': 'Địa lí',
    'VA': 'Ngữ văn', 'Van': 'Ngữ văn', 'Văn': 'Ngữ văn', 'Ngữ văn': 'Ngữ văn',
    'N1_THI': 'Tiếng Anh', 'Anh': 'Tiếng Anh', 'Tiếng Anh': 'Tiếng Anh', 'Ngoai Ngu': 'Tiếng Anh',
    'TI': 'Tiếng Anh'
};

// CÂY TỪ ĐIỂN V-SAT (Các cận b cao nhất đã được nới đến 150, cận a thấp nhất nới về 0)
const VSAT_MATRIX: Record<string, VsatRange[]> = {
    'Toán': [
        { rank: '3%', a: 132.0, b: 150.0, c: 8.5, d: 10.0 },
        { rank: '5%', a: 128.5, b: 132.0, c: 8.1, d: 8.5 },
        { rank: '10%', a: 122.5, b: 128.5, c: 7.75, d: 8.1 },
        { rank: '20%', a: 114.5, b: 122.5, c: 7.0, d: 7.75 },
        { rank: '30%', a: 108.0, b: 114.5, c: 6.6, d: 7.0 },
        { rank: '40%', a: 102.5, b: 108.0, c: 6.25, d: 6.6 },
        { rank: '50%', a: 97.0, b: 102.5, c: 6.0, d: 6.25 },
        { rank: '60%', a: 91.0, b: 97.0, c: 5.6, d: 6.0 },
        { rank: '70%', a: 85.0, b: 91.0, c: 5.25, d: 5.6 },
        { rank: '80%', a: 77.0, b: 85.0, c: 5.0, d: 5.25 },
        { rank: '90%', a: 68.0, b: 77.0, c: 4.5, d: 5.0 },
        { rank: '>90%', a: 0.0, b: 68.0, c: 1.5, d: 4.5 }
    ],
    'Vật lí': [
        { rank: '3%', a: 123.0, b: 150.0, c: 9.5, d: 10.0 },
        { rank: '5%', a: 118.5, b: 123.0, c: 9.25, d: 9.5 },
        { rank: '10%', a: 112.5, b: 118.5, c: 9.0, d: 9.25 },
        { rank: '20%', a: 105.0, b: 112.5, c: 8.5, d: 9.0 },
        { rank: '30%', a: 99.5, b: 105.0, c: 8.0, d: 8.5 },
        { rank: '40%', a: 94.5, b: 99.5, c: 7.75, d: 8.0 },
        { rank: '50%', a: 90.0, b: 94.5, c: 7.5, d: 7.75 },
        { rank: '60%', a: 85.0, b: 90.0, c: 7.25, d: 7.5 },
        { rank: '70%', a: 80.0, b: 85.0, c: 6.75, d: 7.25 },
        { rank: '80%', a: 74.0, b: 80.0, c: 6.35, d: 6.75 },
        { rank: '90%', a: 66.5, b: 74.0, c: 5.75, d: 6.35 },
        { rank: '>90%', a: 0.0, b: 66.5, c: 3.05, d: 5.75 }
    ],
    'Hóa học': [
        { rank: '3%', a: 129.0, b: 150.0, c: 9.5, d: 10.0 },
        { rank: '5%', a: 124.5, b: 129.0, c: 9.25, d: 9.5 },
        { rank: '10%', a: 117.0, b: 124.5, c: 8.75, d: 9.25 },
        { rank: '20%', a: 107.5, b: 117.0, c: 8.25, d: 8.75 },
        { rank: '30%', a: 100.5, b: 107.5, c: 7.75, d: 8.25 },
        { rank: '40%', a: 94.0, b: 100.5, c: 7.25, d: 7.75 },
        { rank: '50%', a: 88.0, b: 94.0, c: 6.75, d: 7.25 },
        { rank: '60%', a: 81.5, b: 88.0, c: 6.25, d: 6.75 },
        { rank: '70%', a: 75.5, b: 81.5, c: 5.75, d: 6.25 },
        { rank: '80%', a: 68.5, b: 75.5, c: 5.25, d: 5.75 },
        { rank: '90%', a: 59.5, b: 68.5, c: 4.6, d: 5.25 },
        { rank: '>90%', a: 0.0, b: 59.5, c: 1.35, d: 4.6 }
    ],
    'Sinh học': [
        { rank: '3%', a: 130.5, b: 150.0, c: 9.0, d: 9.75 },
        { rank: '5%', a: 126.5, b: 130.5, c: 8.75, d: 9.0 },
        { rank: '10%', a: 120.5, b: 126.5, c: 8.34, d: 8.75 },
        { rank: '20%', a: 112.5, b: 120.5, c: 7.85, d: 8.34 },
        { rank: '30%', a: 105.5, b: 112.5, c: 7.5, d: 7.85 },
        { rank: '40%', a: 100.0, b: 105.5, c: 7.25, d: 7.5 },
        { rank: '50%', a: 94.5, b: 100.0, c: 6.85, d: 7.25 },
        { rank: '60%', a: 88.5, b: 94.5, c: 6.5, d: 6.85 },
        { rank: '70%', a: 82.5, b: 88.5, c: 6.25, d: 6.5 },
        { rank: '80%', a: 76.0, b: 82.5, c: 5.85, d: 6.25 },
        { rank: '90%', a: 66.5, b: 76.0, c: 5.25, d: 5.85 },
        { rank: '>90%', a: 0.0, b: 66.5, c: 2.8, d: 5.25 }
    ],
    'Lịch sử': [
        { rank: '3%', a: 133.5, b: 150.0, c: 9.75, d: 10.0 },
        { rank: '5%', a: 131.0, b: 133.5, c: 9.5, d: 9.75 },
        { rank: '10%', a: 126.5, b: 131.0, c: 9.25, d: 9.5 },
        { rank: '20%', a: 120.5, b: 126.5, c: 9.0, d: 9.25 },
        { rank: '30%', a: 115.0, b: 120.5, c: 8.5, d: 9.0 },
        { rank: '40%', a: 110.0, b: 115.0, c: 8.25, d: 8.5 },
        { rank: '50%', a: 105.5, b: 110.0, c: 8.0, d: 8.25 },
        { rank: '60%', a: 101.0, b: 105.5, c: 7.75, d: 8.0 },
        { rank: '70%', a: 95.5, b: 101.0, c: 7.5, d: 7.75 },
        { rank: '80%', a: 88.5, b: 95.5, c: 7.0, d: 7.5 },
        { rank: '90%', a: 79.5, b: 88.5, c: 6.35, d: 7.0 },
        { rank: '>90%', a: 0.0, b: 79.5, c: 2.95, d: 6.35 }
    ],
    'Địa lí': [
        { rank: '3%', a: 124.0, b: 150.0, c: 10.0, d: 10.0 },
        { rank: '5%', a: 120.5, b: 124.0, c: 10.0, d: 10.0 },
        { rank: '10%', a: 115.5, b: 120.5, c: 9.75, d: 10.0 },
        { rank: '20%', a: 108.5, b: 115.5, c: 9.25, d: 9.75 },
        { rank: '30%', a: 103.0, b: 108.5, c: 9.0, d: 9.25 },
        { rank: '40%', a: 98.5, b: 103.0, c: 8.75, d: 9.0 },
        { rank: '50%', a: 94.0, b: 98.5, c: 8.5, d: 8.75 },
        { rank: '60%', a: 89.5, b: 94.0, c: 8.25, d: 8.5 },
        { rank: '70%', a: 84.5, b: 89.5, c: 7.75, d: 8.25 },
        { rank: '80%', a: 79.0, b: 84.5, c: 7.25, d: 7.75 },
        { rank: '90%', a: 71.0, b: 79.0, c: 6.5, d: 7.25 },
        { rank: '>90%', a: 0.0, b: 71.0, c: 3.0, d: 6.5 }
    ],
    'Tiếng Anh': [
        { rank: '3%', a: 131.0, b: 150.0, c: 7.75, d: 9.75 }, // Theo bảng, > 9.75 max
        { rank: '5%', a: 127.5, b: 131.0, c: 7.5, d: 7.75 },
        { rank: '10%', a: 120.5, b: 127.5, c: 7.0, d: 7.5 },
        { rank: '20%', a: 112.0, b: 120.5, c: 6.5, d: 7.0 },
        { rank: '30%', a: 105.0, b: 112.0, c: 6.0, d: 6.5 },
        { rank: '40%', a: 98.5, b: 105.0, c: 5.75, d: 6.0 },
        { rank: '50%', a: 92.0, b: 98.5, c: 5.5, d: 5.75 },
        { rank: '60%', a: 85.5, b: 92.0, c: 5.25, d: 5.5 },
        { rank: '70%', a: 78.5, b: 85.5, c: 5.0, d: 5.25 },
        { rank: '80%', a: 70.5, b: 78.5, c: 4.5, d: 5.0 },
        { rank: '90%', a: 60.0, b: 70.5, c: 4.0, d: 4.5 },
        { rank: '>90%', a: 0.0, b: 60.0, c: 1.25, d: 4.0 }
    ],
    'Ngữ văn': [
        { rank: '3%', a: 129.5, b: 150.0, c: 9.25, d: 9.75 },
        { rank: '5%', a: 127.5, b: 129.5, c: 9.0, d: 9.25 },
        { rank: '10%', a: 124.0, b: 127.5, c: 9.0, d: 9.0 },
        { rank: '20%', a: 119.5, b: 124.0, c: 8.75, d: 9.0 },
        { rank: '30%', a: 115.5, b: 119.5, c: 8.5, d: 8.75 },
        { rank: '40%', a: 112.5, b: 115.5, c: 8.25, d: 8.5 },
        { rank: '50%', a: 109.0, b: 112.5, c: 8.0, d: 8.25 },
        { rank: '60%', a: 106.0, b: 109.0, c: 7.75, d: 8.0 },
        { rank: '70%', a: 102.0, b: 106.0, c: 7.5, d: 7.75 },
        { rank: '80%', a: 97.0, b: 102.0, c: 7.25, d: 7.5 },
        { rank: '90%', a: 90.0, b: 97.0, c: 6.75, d: 7.25 },
        { rank: '>90%', a: 0.0, b: 90.0, c: 3.5, d: 6.75 }
    ]
};

export const convertVsatToThpt = (monHocAlias: string | undefined, diemVsat: number): number => {
    if (!monHocAlias) return 0;
    
    // 1. Ánh xạ Tên Môn
    const standardName = SUBJECT_MAP[monHocAlias];
    if (!standardName) {
        throw new Error(`[VSAT_ERROR] Môn học "${monHocAlias}" không được hỗ trợ quy đổi V-SAT.`);
    }

    // 2. Lấy Matrix của Môn đó
    const matrix = VSAT_MATRIX[standardName];
    if (!matrix) {
        throw new Error(`[VSAT_ERROR] Chưa cấu hình Ma trận V-SAT cho môn "${standardName}".`);
    }

    // 3. Xử lý các case đặc biệt (Out of bounds)
    if (diemVsat <= 0) return 0;
    if (diemVsat > 150) diemVsat = 150;

    // 4. Tìm khoảng phân vị chứa điểm x (a < x <= b)
    const range = matrix.find(r => diemVsat > r.a && diemVsat <= r.b);

    if (!range) {
        // Fallback an toàn, dù logic a=0 b=150 nên bắt được tất cả
        throw new Error(`[VSAT_ERROR] Điểm ${diemVsat} của môn ${standardName} không nằm trong bất kỳ khoảng quy đổi nào.`);
    }

    // 5. Áp dụng Công thức Nội suy
    const { a, b, c, d } = range;
    
    // Nếu điểm bằng chính a hoặc a == b (tránh lỗi chia cho 0)
    if (a === b) return c;
    if (diemVsat === a) return c; // điểm ko thể = a vì đk là > a nhưng cẩn tắc vô áy náy

    const diemThpt = c + ((diemVsat - a) / (b - a)) * (d - c);

    // 6. Làm tròn 2 chữ số thập phân (Quy chuẩn của Bộ)
    return Math.round(diemThpt * 100) / 100;
};
