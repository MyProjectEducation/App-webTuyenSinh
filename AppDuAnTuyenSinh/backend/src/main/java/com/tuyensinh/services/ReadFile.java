package com.tuyensinh.services;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.*;

import com.tuyensinh.models.Candidate;
import com.tuyensinh.models.CandidateScore;
import com.tuyensinh.DAO.CandidateScoreDAO;
import com.tuyensinh.DAO.CandidateDAO;

public class ReadFile {
    public static void readFileCandidate(String filePath) {
        try {
            // 1. Mở file Excel
            FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = WorkbookFactory.create(fis);

            // 2. Lấy Sheet đầu tiên (index 0)
            Sheet sheet = workbook.getSheetAt(0);

            // 3. Duyệt qua từng dòng (Row)
            for (Row row : sheet) {
                // Bỏ qua dòng tiêu đề (nếu có)
                if (row.getRowNum() == 0) {
                    continue;
                }
                // 4. Tạo đối tượng Candidate từ dữ liệu trong dòng
                Candidate candidate = new Candidate();

                candidate.setCccd(row.getCell(1).getStringCellValue());
                candidate.setSobaodanh("00000000");
                candidate.setHo(row.getCell(2).getStringCellValue());
                candidate.setTen(row.getCell(2).getStringCellValue());
                candidate.setNgaySinh(row.getCell(3).getStringCellValue());
                candidate.setDienThoai("00000000");
                candidate.setPassword("123456");
                candidate.setGioiTinh(row.getCell(4).getStringCellValue());
                candidate.setEmail("0@gmail.com");
                candidate.setNoiSinh(row.getCell(35).getStringCellValue());
                candidate.setUpdatedAt(LocalDate.now().toString());
                candidate.setDoiTuong(row.getCell(5).getStringCellValue());
                candidate.setKhuVuc(row.getCell(6).getStringCellValue());

                CandidateDAO.createCandidate(candidate);
            }

            workbook.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi đọc file Excel!");
        }
    }

    public static void readFileScoreCandidate(String filePath) {
        // 1. Mở file Excel
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = WorkbookFactory.create(fis);

            // 2. Lấy Sheet đầu tiên (index 0)
            Sheet sheet = workbook.getSheetAt(0);

            // 3. Duyệt qua từng dòng (Row)
            for (Row row : sheet) {
                // Bỏ qua dòng tiêu đề (nếu có)
                if (row.getRowNum() == 0) {
                    continue;
                }
                // 4. Tạo đối tượng CandidateScore từ dữ liệu trong dòng
                CandidateScore candidatesScore = new CandidateScore();

                candidatesScore.setCccd(row.getCell(1).getStringCellValue());
                candidatesScore.setSobaodanh("00000000");
                candidatesScore.setD_phuongthuc("THPT");
                candidatesScore.setTo(row.getCell(7).getNumericCellValue());
                candidatesScore.setVa(row.getCell(8).getNumericCellValue());
                candidatesScore.setLi(row.getCell(9).getNumericCellValue());
                candidatesScore.setHo(row.getCell(10).getNumericCellValue());
                candidatesScore.setSi(row.getCell(11).getNumericCellValue());
                candidatesScore.setSu(row.getCell(12).getNumericCellValue());
                candidatesScore.setDi(row.getCell(13).getNumericCellValue());
                candidatesScore.setN1_thi(0.0);
                candidatesScore.setN1_cc(0.0);
                candidatesScore.setCncn(row.getCell(19).getNumericCellValue());
                candidatesScore.setCnnn(row.getCell(20).getNumericCellValue());
                candidatesScore.setTi(row.getCell(18).getNumericCellValue());
                candidatesScore.setKtpl(row.getCell(17).getNumericCellValue());
                candidatesScore.setNl1(0.0);
                candidatesScore.setNk1(row.getCell(22).getNumericCellValue());
                candidatesScore.setNk2(row.getCell(23).getNumericCellValue());

                CandidateScoreDAO.createCandidateScore(candidatesScore);
            }

            workbook.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi đọc file Excel!");
        }
    }
}
