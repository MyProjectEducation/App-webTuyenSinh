package com.tuyensinh.services;

import com.tuyensinh.models.Candidate;
import com.tuyensinh.models.Combination;
import com.tuyensinh.models.Program;
import com.tuyensinh.models.ProgramCombination;
import com.tuyensinh.models.CandidateScore;
import com.tuyensinh.models.BonusPoint;
import com.tuyensinh.models.Aspiration;
import com.tuyensinh.models.Conversion;

import com.tuyensinh.DAO.CandidateDAO;
import com.tuyensinh.DAO.CandidateScoreDAO;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
        private static DataStore instance;

        public List<Candidate> candidates;
        public List<Combination> combinations;
        public List<Program> programs;
        public List<ProgramCombination> programCombinations;
        public List<CandidateScore> scores;
        public List<BonusPoint> bonusPoints;
        public List<Aspiration> aspirations;
        public List<Conversion> conversions;
        public List<com.tuyensinh.models.AppUser> appUsers;

        private DataStore() {
                appUsers = new java.util.ArrayList<>();
                appUsers.add(new com.tuyensinh.models.AppUser(1, "admin", "123456", "Administrator", "ADMIN", true));

                candidates = new ArrayList<>();
                candidates = CandidateDAO.getAllCandidates(); // Lấy dữ liệu thí sinh từ database thông qua DAO

                combinations = new ArrayList<>();
                combinations.add(new Combination(2, "A01", "TO", "LI", "N1", "Toán, Vật lí, Tiếng Anh"));
                combinations.add(new Combination(5, "B00", "TO", "HO", "SI", "Toán, Hóa học, Sinh học"));
                combinations.add(new Combination(6, "C00", "VA", "SU", "DI", "Ngữ văn, Lịch sử, Địa lí"));

                programs = new ArrayList<>();
                programs.add(new Program(1, "7480201", "Công nghệ thông tin", "A00", 500, 21.5, 0.0));
                programs.add(new Program(2, "7480104", "Hệ thống thông tin", "A01", 300, 20.0, 0.0));

                programCombinations = new ArrayList<>();
                programCombinations.add(
                                new ProgramCombination(1, "7140114", "B03", "TO", 3, "VA", 3, "SI", 1, "7140114_B03", 0,
                                                1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0.0));
                programCombinations.add(
                                new ProgramCombination(2, "7140114", "C01", "TO", 3, "VA", 3, "LI", 1, "7140114_C01", 0,
                                                1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0.0));

                scores = new ArrayList<>();
                scores = CandidateScoreDAO.getAllCandidateScores(); // Lấy dữ liệu điểm thi của thí sinh từ database
                                                                    // thông qua DAO

                bonusPoints = new ArrayList<>();
                bonusPoints
                                .add(new BonusPoint(1, "001207008593", "7480201", "A00", "THPT", 0.5, 0.5, 1.0,
                                                "Ghi chú...", "KEY1"));

                aspirations = new ArrayList<>();
                aspirations.add(new Aspiration(2, "001207000045", "7480107", 7, 19.95, 0.0, 0.0, 19.95, "duoisan",
                                "001207000045_7480107_PT2", "PT2", ""));
                aspirations.add(new Aspiration(8, "001207008593", "7310401", 4, 20.25, 0.0, 0.0, 20.25, "yes",
                                "001207008593_7310401_PT2", "PT2", ""));
                aspirations.add(new Aspiration(12, "001207012439", "7480201", 9, 23.83, 0.21, 0.0, 24.04, "yes",
                                "001207012439_7480201_PT2", "PT2", ""));

                conversions = new ArrayList<>();
                conversions.add(new Conversion(2, "DGNL", "A01", "", 998.0, 1018.0, 26.25, 26.75, "DGNL_A01_2", "2"));
                conversions.add(new Conversion(3, "DGNL", "A01", "", 984.0, 997.0, 25.75, 26.10, "DGNL_A01_3", "3"));
                conversions.add(new Conversion(4, "DGNL", "A01", "", 973.0, 983.0, 25.35, 25.65, "DGNL_A01_4", "4"));
        }

        public static DataStore getInstance() {
                if (instance == null) {
                        instance = new DataStore();
                }
                return instance;
        }
}
