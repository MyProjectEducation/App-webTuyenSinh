package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VsatConverter {

    public static class Range {
        public double a, b, c, d;

        public Range(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

    private static final Map<String, String> SUBJECT_MAP = new HashMap<>();
    private static final Map<String, List<Range>> VSAT_MATRIX = new HashMap<>();

    static {
        // 1. Bản đồ chuẩn hóa tên môn học
        SUBJECT_MAP.put("TO", "Toán");
        SUBJECT_MAP.put("TOAN", "Toán");
        SUBJECT_MAP.put("TOÁN", "Toán");
        
        SUBJECT_MAP.put("LI", "Vật lí");
        SUBJECT_MAP.put("LY", "Vật lí");
        SUBJECT_MAP.put("VẬT LÝ", "Vật lí");
        SUBJECT_MAP.put("VẬT LÍ", "Vật lí");
        
        SUBJECT_MAP.put("HO", "Hóa học");
        SUBJECT_MAP.put("HOA", "Hóa học");
        SUBJECT_MAP.put("HÓA", "Hóa học");
        SUBJECT_MAP.put("HÓA HỌC", "Hóa học");
        
        SUBJECT_MAP.put("SI", "Sinh học");
        SUBJECT_MAP.put("SINH", "Sinh học");
        SUBJECT_MAP.put("SINH HỌC", "Sinh học");
        
        SUBJECT_MAP.put("SU", "Lịch sử");
        SUBJECT_MAP.put("SU", "Lịch sử");
        SUBJECT_MAP.put("SỬ", "Lịch sử");
        SUBJECT_MAP.put("LỊCH SỬ", "Lịch sử");
        
        SUBJECT_MAP.put("DI", "Địa lí");
        SUBJECT_MAP.put("DIA", "Địa lí");
        SUBJECT_MAP.put("ĐỊA", "Địa lí");
        SUBJECT_MAP.put("ĐỊA LÝ", "Địa lí");
        SUBJECT_MAP.put("ĐỊA LÍ", "Địa lí");
        
        SUBJECT_MAP.put("VA", "Ngữ văn");
        SUBJECT_MAP.put("VAN", "Ngữ văn");
        SUBJECT_MAP.put("VĂN", "Ngữ văn");
        SUBJECT_MAP.put("NGỮ VĂN", "Ngữ văn");
        
        SUBJECT_MAP.put("N1", "Tiếng Anh");
        SUBJECT_MAP.put("ANH", "Tiếng Anh");
        SUBJECT_MAP.put("TIẾNG ANH", "Tiếng Anh");
        SUBJECT_MAP.put("NGOAI NGU", "Tiếng Anh");
        SUBJECT_MAP.put("TI", "Tiếng Anh");

        // 2. Khởi tạo ma trận phân vị chi tiết 8 môn (đồng bộ 100% với React/NodeJS)
        
        // --- TOÁN ---
        List<Range> toan = new ArrayList<>();
        toan.add(new Range(132.0, 150.0, 8.5, 10.0));
        toan.add(new Range(128.5, 132.0, 8.1, 8.5));
        toan.add(new Range(122.5, 128.5, 7.75, 8.1));
        toan.add(new Range(114.5, 122.5, 7.0, 7.75));
        toan.add(new Range(108.0, 114.5, 6.6, 7.0));
        toan.add(new Range(102.5, 108.0, 6.25, 6.6));
        toan.add(new Range(97.0, 102.5, 6.0, 6.25));
        toan.add(new Range(91.0, 97.0, 5.6, 6.0));
        toan.add(new Range(85.0, 91.0, 5.25, 5.6));
        toan.add(new Range(77.0, 85.0, 5.0, 5.25));
        toan.add(new Range(68.0, 77.0, 4.5, 5.0));
        toan.add(new Range(0.0, 68.0, 1.5, 4.5));
        VSAT_MATRIX.put("Toán", toan);

        // --- VẬT LÍ ---
        List<Range> vatli = new ArrayList<>();
        vatli.add(new Range(123.0, 150.0, 9.5, 10.0));
        vatli.add(new Range(118.5, 123.0, 9.25, 9.5));
        vatli.add(new Range(112.5, 118.5, 9.0, 9.25));
        vatli.add(new Range(105.0, 112.5, 8.5, 9.0));
        vatli.add(new Range(99.5, 105.0, 8.0, 8.5));
        vatli.add(new Range(94.5, 99.5, 7.75, 8.0));
        vatli.add(new Range(90.0, 94.5, 7.5, 7.75));
        vatli.add(new Range(85.0, 90.0, 7.25, 7.5));
        vatli.add(new Range(80.0, 85.0, 6.75, 7.25));
        vatli.add(new Range(74.0, 80.0, 6.35, 6.75));
        vatli.add(new Range(66.5, 74.0, 5.75, 6.35));
        vatli.add(new Range(0.0, 66.5, 3.05, 5.75));
        VSAT_MATRIX.put("Vật lí", vatli);

        // --- HÓA HỌC ---
        List<Range> hoahoc = new ArrayList<>();
        hoahoc.add(new Range(129.0, 150.0, 9.5, 10.0));
        hoahoc.add(new Range(124.5, 129.0, 9.25, 9.5));
        hoahoc.add(new Range(117.0, 124.5, 8.75, 9.25));
        hoahoc.add(new Range(107.5, 117.0, 8.25, 8.75));
        hoahoc.add(new Range(100.5, 107.5, 7.75, 8.25));
        hoahoc.add(new Range(94.0, 100.5, 7.25, 7.75));
        hoahoc.add(new Range(88.0, 94.0, 6.75, 7.25));
        hoahoc.add(new Range(81.5, 88.0, 6.25, 6.75));
        hoahoc.add(new Range(75.5, 81.5, 5.75, 6.25));
        hoahoc.add(new Range(68.5, 75.5, 5.25, 5.75));
        hoahoc.add(new Range(59.5, 68.5, 4.6, 5.25));
        hoahoc.add(new Range(0.0, 59.5, 1.35, 4.6));
        VSAT_MATRIX.put("Hóa học", hoahoc);

        // --- SINH HỌC ---
        List<Range> sinhhoc = new ArrayList<>();
        sinhhoc.add(new Range(130.5, 150.0, 9.0, 9.75));
        sinhhoc.add(new Range(126.5, 130.5, 8.75, 9.0));
        sinhhoc.add(new Range(120.5, 126.5, 8.34, 8.75));
        sinhhoc.add(new Range(112.5, 120.5, 7.85, 8.34));
        sinhhoc.add(new Range(105.5, 112.5, 7.5, 7.85));
        sinhhoc.add(new Range(100.0, 105.5, 7.25, 7.5));
        sinhhoc.add(new Range(94.5, 100.0, 6.85, 7.25));
        sinhhoc.add(new Range(88.5, 94.5, 6.5, 6.85));
        sinhhoc.add(new Range(82.5, 88.5, 6.25, 6.5));
        sinhhoc.add(new Range(76.0, 82.5, 5.85, 6.25));
        sinhhoc.add(new Range(66.5, 76.0, 5.25, 5.85));
        sinhhoc.add(new Range(0.0, 66.5, 2.8, 5.25));
        VSAT_MATRIX.put("Sinh học", sinhhoc);

        // --- LỊCH SỬ ---
        List<Range> lichsu = new ArrayList<>();
        lichsu.add(new Range(133.5, 150.0, 9.75, 10.0));
        lichsu.add(new Range(131.0, 133.5, 9.5, 9.75));
        lichsu.add(new Range(126.5, 131.0, 9.25, 9.5));
        lichsu.add(new Range(120.5, 126.5, 9.0, 9.25));
        lichsu.add(new Range(115.0, 120.5, 8.5, 9.0));
        lichsu.add(new Range(110.0, 115.0, 8.25, 8.5));
        lichsu.add(new Range(105.5, 110.0, 8.0, 8.25));
        lichsu.add(new Range(101.0, 105.5, 7.75, 8.0));
        lichsu.add(new Range(95.5, 101.0, 7.5, 7.75));
        lichsu.add(new Range(88.5, 95.5, 7.0, 7.5));
        lichsu.add(new Range(79.5, 88.5, 6.35, 7.0));
        lichsu.add(new Range(0.0, 79.5, 2.95, 6.35));
        VSAT_MATRIX.put("Lịch sử", lichsu);

        // --- ĐỊA LÍ ---
        List<Range> diali = new ArrayList<>();
        diali.add(new Range(124.0, 150.0, 10.0, 10.0));
        diali.add(new Range(120.5, 124.0, 10.0, 10.0));
        diali.add(new Range(115.5, 120.5, 9.75, 10.0));
        diali.add(new Range(108.5, 115.5, 9.25, 9.75));
        diali.add(new Range(103.0, 108.5, 9.0, 9.25));
        diali.add(new Range(98.5, 103.0, 8.75, 9.0));
        diali.add(new Range(94.0, 98.5, 8.5, 8.75));
        diali.add(new Range(89.5, 94.0, 8.25, 8.5));
        diali.add(new Range(84.5, 89.5, 7.75, 8.25));
        diali.add(new Range(79.0, 84.5, 7.25, 7.75));
        diali.add(new Range(71.0, 79.0, 6.5, 7.25));
        diali.add(new Range(0.0, 71.0, 3.0, 6.5));
        VSAT_MATRIX.put("Địa lí", diali);

        // --- TIẾNG ANH ---
        List<Range> tienganh = new ArrayList<>();
        tienganh.add(new Range(131.0, 150.0, 7.75, 9.75));
        tienganh.add(new Range(127.5, 131.0, 7.5, 7.75));
        tienganh.add(new Range(120.5, 127.5, 7.0, 7.5));
        tienganh.add(new Range(112.0, 120.5, 6.5, 7.0));
        tienganh.add(new Range(105.0, 112.0, 6.0, 6.5));
        tienganh.add(new Range(98.5, 105.0, 5.75, 6.0));
        tienganh.add(new Range(92.0, 98.5, 5.5, 5.75));
        tienganh.add(new Range(85.5, 92.0, 5.25, 5.5));
        tienganh.add(new Range(78.5, 85.5, 5.0, 5.25));
        tienganh.add(new Range(70.5, 78.5, 4.5, 5.0));
        tienganh.add(new Range(60.0, 70.5, 4.0, 4.5));
        tienganh.add(new Range(0.0, 60.0, 1.25, 4.0));
        VSAT_MATRIX.put("Tiếng Anh", tienganh);

        // --- NGỮ VĂN ---
        List<Range> nguvan = new ArrayList<>();
        nguvan.add(new Range(129.5, 150.0, 9.25, 9.75));
        nguvan.add(new Range(127.5, 129.5, 9.0, 9.25));
        nguvan.add(new Range(124.0, 127.5, 9.0, 9.0));
        nguvan.add(new Range(119.5, 124.0, 8.75, 9.0));
        nguvan.add(new Range(115.5, 119.5, 8.5, 8.75));
        nguvan.add(new Range(112.5, 115.5, 8.25, 8.5));
        nguvan.add(new Range(109.0, 112.5, 8.0, 8.25));
        nguvan.add(new Range(106.0, 109.0, 7.75, 8.0));
        nguvan.add(new Range(102.0, 106.0, 7.5, 7.75));
        nguvan.add(new Range(97.0, 102.0, 7.25, 7.5));
        nguvan.add(new Range(90.0, 97.0, 6.75, 7.25));
        nguvan.add(new Range(0.0, 90.0, 3.5, 6.75));
        VSAT_MATRIX.put("Ngữ văn", nguvan);
    }

    /**
     * Chuyển đổi điểm V-SAT thang 150 sang điểm THPT thang 10
     * @param subjectCode Mã môn học (ví dụ: 'TO', 'TOAN', 'Toán'...)
     * @param vsatScore Điểm V-SAT gốc
     * @return Điểm THPT quy đổi đã làm tròn 2 chữ số thập phân
     */
    public static BigDecimal convert(String subjectCode, BigDecimal vsatScore) {
        if (vsatScore == null) return BigDecimal.ZERO;
        if (subjectCode == null || subjectCode.trim().isEmpty()) return BigDecimal.ZERO;

        // 1. Chuẩn hóa tên môn học
        String standardName = SUBJECT_MAP.get(subjectCode.trim().toUpperCase());
        if (standardName == null) {
            System.err.println("[VsatConverter] Lỗi: Không thể chuẩn hóa môn " + subjectCode);
            return BigDecimal.ZERO;
        }

        // 2. Lấy ma trận bách phân vị tương ứng
        List<Range> matrix = VSAT_MATRIX.get(standardName);
        if (matrix == null || matrix.isEmpty()) {
            System.err.println("[VsatConverter] Lỗi: Chưa cấu hình Ma trận V-SAT cho môn " + standardName);
            return BigDecimal.ZERO;
        }

        // 3. Giới hạn cận trên và dưới
        double score = vsatScore.doubleValue();
        if (score <= 0) return BigDecimal.ZERO;
        if (score > 150) score = 150;

        // 4. Tìm khoảng bách phân vị chứa điểm (a < x <= b)
        Range matchedRange = null;
        for (Range r : matrix) {
            if (score > r.a && score <= r.b) {
                matchedRange = r;
                break;
            }
        }

        // Xử lý ngoại lệ: Điểm cực thấp dưới mốc a thấp nhất
        if (matchedRange == null) {
            Range lowestRange = matrix.get(matrix.size() - 1);
            if (score <= lowestRange.a) {
                return BigDecimal.valueOf(lowestRange.c).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }

        // 5. Tính toán nội suy tuyến tính: y = c + ((x - a) / (b - a)) * (d - c)
        if (matchedRange.a == matchedRange.b) {
            return BigDecimal.valueOf(matchedRange.c).setScale(2, RoundingMode.HALF_UP);
        }
        
        double converted = matchedRange.c + ((score - matchedRange.a) / (matchedRange.b - matchedRange.a)) * (matchedRange.d - matchedRange.c);
        
        // 6. Làm tròn 2 chữ số thập phân
        return BigDecimal.valueOf(converted).setScale(2, RoundingMode.HALF_UP);
    }
}
