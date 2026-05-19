package ui.panels;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import ui.dialogs.DiemThiSinhDialog;
import ui.icon.DrawHamburger;
import entity.DiemThiSinh;
import entity.ThiSinh;
import dao.ThiSinhDAO;
import dao.DiemThiSinhDAO;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.io.File;

import org.apache.poi.ss.formula.atp.Switch;
import org.apache.poi.ss.usermodel.*;

public class DiemThiPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private List<DiemThiSinh> filteredData;

    // Columns matching xt_diemthixettuyen schema
    private static final String[] COLUMNS = {
            "ID", "CCCD", "Số báo danh", "Phương thức",
            "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Ngữ văn",
            "Anh (thi)", "Anh (chứng chỉ)", "Công nghệ CN", "Công nghệ NN", "Tin học", "KTPL",
            "Năng khiếu 1", "Năng khiếu 2", "Hành động"
    };

    private static List<DiemThiSinh> DATA = DiemThiSinhDAO.getAllCandidateScores();
    static {
        DATA.sort((a, b) -> Integer.compare(a.getIddiemthi(), b.getIddiemthi()));
    }

    public DiemThiPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel(THPT)");
        RoundButton btnImportDgnl = RoundButton.secondary("Import Excel(ĐGNL, V-SAT)");
        RoundButton btnAdd = RoundButton.primary("+ Thêm điểm");
        btnImport.addActionListener(e -> showImportDialog("THPT"));
        btnImportDgnl.addActionListener(e -> showImportDialog("ĐGNL, V-SAT"));
        btnAdd.addActionListener(e -> {
            DiemThiSinh newScore = DiemThiSinhDialog.showDialog(mainFrame, null);
            if (newScore != null && newScore.getCccd() != null && !newScore.getCccd().trim().isEmpty()) {
                DATA = DiemThiSinhDAO.getAllCandidateScores();
                DATA.sort((a, b) -> Integer.compare(a.getIddiemthi(), b.getIddiemthi()));
                loadData(DATA);
            }
        });

        JPanel topBar = buildTopBar(
                "Điểm thi",
                "Điểm THPT, ĐGNL, V-SAT (quy đổi thang 30 khi xét tuyển)", btnImport, btnImportDgnl, btnAdd);

        JPanel statRow = new JPanel(new GridLayout(1, 4, 10, 0));
        statRow.setOpaque(false);
        statRow.setBorder(new EmptyBorder(10, 14, 8, 14));
        int THPTCount = 0, VSATCount = 0, DGNLCount = 0;
        for (DiemThiSinh score : DATA) {
            if (score.getTo() != null || score.getLi() != null || score.getHo() != null || score.getSi() != null
                    || score.getSu() != null || score.getDi() != null || score.getVa() != null) {
                THPTCount++;
            }
            if (score.getTO_VS() != null || score.getLI_VS() != null || score.getHO_VS() != null
                    || score.getSI_VS() != null
                    || score.getSU_VS() != null || score.getDI_VS() != null || score.getN1_VS() != null
                    || score.getTO_NL() != null
                    || score.getLI_NL() != null || score.getHO_NL() != null || score.getVA_NL() != null
                    || score.getSI_NL() != null || score.getSU_NL() != null || score.getDI_NL() != null) {
                VSATCount++;
            }
            if (score.getNl1() != null || score.getNl2() != null) {
                DGNLCount++;
            }
        }
        statRow.add(UIComponents.statCard("THPT", String.valueOf(THPTCount), AppTheme.PRIMARY, "Hồ sơ có điểm"));
        statRow.add(UIComponents.statCard("V-SAT", String.valueOf(VSATCount), AppTheme.GREEN, "Hồ sơ có điểm"));
        statRow.add(UIComponents.statCard("ĐGNL", String.valueOf(DGNLCount), AppTheme.AMBER, "Hồ sơ có điểm"));

        txtSearch = UIComponents.searchField("Tìm CCCD, số báo danh...");
        txtSearch.setPreferredSize(new Dimension(240, 30));
        RoundButton btnSearch = RoundButton.secondary("Tìm");
        btnSearch.addActionListener(e -> doSearch());

        JPanel searchBar = buildSearchBar(
                new JLabel("  Tìm: "), txtSearch, btnSearch);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData(DATA);

        int[] widths = { 55, 110, 110, 75, 45, 45, 45, 45, 45, 45, 45, 55, 55, 50, 50, 45, 50, 45, 50, 50, 80 };
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Center-align score columns
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 4; i < COLUMNS.length - 1; i++)
            table.getColumnModel().getColumn(i).setCellRenderer(new ScoreCellRenderer());

        // Phuong thuc badge renderer
        table.getColumnModel().getColumn(3).setCellRenderer(new PhuongThucRenderer());
        table.getColumn("Hành động").setCellRenderer(new ActionRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COLUMNS.length - 1 && row >= 0) {
                    handleRowAction(row, e.getX(), e.getY());
                }
            }
        });

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(topBar, BorderLayout.NORTH);
        JPanel topBody = new JPanel(new BorderLayout());
        topBody.add(statRow, BorderLayout.NORTH);
        topBody.add(searchBar, BorderLayout.SOUTH);
        topSection.add(topBody, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(topSection, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData(List<DiemThiSinh> scores) {
        tableModel.setRowCount(0);
        for (DiemThiSinh score : scores) {
            String method = score.getD_phuongthuc();
            boolean isVsat = "PT3".equals(method) || "VSAT".equals(method);

            Object toScore = isVsat ? util.VsatConverter.convert("TO", score.getTO_VS()) : score.getTo();
            Object liScore = isVsat ? util.VsatConverter.convert("LI", score.getLI_VS()) : score.getLi();
            Object hoScore = isVsat ? util.VsatConverter.convert("HO", score.getHO_VS()) : score.getHo();
            Object siScore = isVsat ? util.VsatConverter.convert("SI", score.getSI_VS()) : score.getSi();
            Object suScore = isVsat ? util.VsatConverter.convert("SU", score.getSU_VS()) : score.getSu();
            Object diScore = isVsat ? util.VsatConverter.convert("DI", score.getDI_VS()) : score.getDi();
            Object vaScore = isVsat ? util.VsatConverter.convert("VA", score.getVA_VS()) : score.getVa();
            Object n1Score = isVsat ? util.VsatConverter.convert("N1", score.getN1_VS()) : score.getN1_thi();

            Object[] row = {
                    score.getIddiemthi(),
                    score.getCccd(),
                    score.getSobaodanh(),
                    score.getD_phuongthuc(),
                    toScore,
                    liScore,
                    hoScore,
                    siScore,
                    suScore,
                    diScore,
                    vaScore,
                    n1Score,
                    score.getN1_cc(),
                    score.getCncn(),
                    score.getCnnn(),
                    score.getTi(),
                    score.getKtpl(),
                    score.getNk1(),
                    score.getNk2()
            };
            tableModel.addRow(row);
        }
    }

    private void doSearch() {
        DATA = DiemThiSinhDAO.getAllCandidateScores();
        String query = txtSearch.getText().trim().toLowerCase();

        filteredData = new ArrayList<>();
        for (DiemThiSinh score : DATA) {
            boolean matchesQuery = query.isEmpty() ||
                    (score.getCccd() != null && score.getCccd().toLowerCase().contains(query)) ||
                    (score.getSobaodanh() != null && score.getSobaodanh().toLowerCase().contains(query));

            if (matchesQuery) {
                filteredData.add(score);
            }
        }
        if (query == "") {
            filteredData = DATA;
        }
        loadData(filteredData);
    }

    private void handleRowAction(int row, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem view = new JMenuItem("👁 Xem chi tiết");
        JMenuItem edit = new JMenuItem("✏ Sửa điểm");
        JMenuItem del = new JMenuItem("🗑 Xóa");
        view.addActionListener(e -> {
            Object cccdObj = tableModel.getValueAt(row, 1);
            DiemThiSinhDialog.showDetailDialog(mainFrame, String.valueOf(cccdObj));
        });
        edit.addActionListener(e -> {
            Object cccdObj = tableModel.getValueAt(row, 1);
            DiemThiSinhDialog.showDialog(mainFrame, String.valueOf(cccdObj));
            // Tải lại dữ liệu sau khi sửa để cập nhật điểm mới và cột Phương thức
            DATA = DiemThiSinhDAO.getAllCandidateScores();
            DATA.sort((a, b) -> Integer.compare(a.getIddiemthi(), b.getIddiemthi()));
            loadData(DATA);
        });
        del.addActionListener(e -> {
            Object cccdObj = tableModel.getValueAt(row, 1);
            DiemThiSinhDAO.deleteCandidateScore(DiemThiSinhDAO.getCandidateScoreByCCCD(String.valueOf(cccdObj)));

            tableModel.removeRow(row);
        });
        menu.add(view);
        menu.add(edit);
        menu.addSeparator();
        menu.add(del);
        menu.show(table, x, y);
    }

    // Score cell renderer: null → "NULL" in gray, 0 → "0" in light, value → bold
    static class ScoreCellRenderer extends DefaultTableCellRenderer {
        public ScoreCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if (v == null) {
                setText("NULL");
                setForeground(AppTheme.TEXT_THIRD);
                setFont(AppTheme.FONT_SMALL);
            } else {
                double d = Double.parseDouble(v.toString());
                setText(String.format("%.2f", d));
                if (d == 0) {
                    setForeground(AppTheme.TEXT_THIRD);
                    setFont(AppTheme.FONT_SMALL);
                } else {
                    setForeground(AppTheme.TEXT_PRIMARY);
                    setFont(AppTheme.FONT_BOLD);
                }
            }
            if (!sel)
                setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
            return c;
        }
    }

    static class PhuongThucRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if ("PT4".equals(v) || "ĐGNL".equals(v)) {
                setText("ĐGNL");
                setForeground(AppTheme.PRIMARY);
                setBackground(AppTheme.PRIMARY_LIGHT);
            } else if ("PT2".equals(v) || "THPT".equals(v)) {
                setText("THPT");
                setForeground(AppTheme.GREEN);
                setBackground(AppTheme.GREEN_LIGHT);
            } else if ("PT3".equals(v) || "VSAT".equals(v)) {
                setText("VSAT");
                setForeground(AppTheme.AMBER);
                setBackground(AppTheme.AMBER_LIGHT);
            } else {
                setText(v != null ? v.toString() : "");
                setForeground(AppTheme.TEXT_SECOND);
                setBackground(AppTheme.BG_SECONDARY);
            }
            if (sel)
                setBackground(AppTheme.PRIMARY_LIGHT);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(AppTheme.FONT_SMALL);
            return this;
        }
    }

    static class ActionRenderer extends DefaultTableCellRenderer {
        private final JPanel panel;

        public ActionRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 2));
            panel.setOpaque(true);
            JButton btnmenu = new JButton(new DrawHamburger());
            btnmenu.setFocusPainted(false);
            panel.add(btnmenu);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            panel.setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
            return panel;
        }
    }

    private void showImportDialog(String LoaiDiem) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file Excel — Thí sinh (" + LoaiDiem + ")");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx, *.xls)", "xlsx", "xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            if (LoaiDiem.equals("THPT")) {
                readFileScoreCandidate(fc.getSelectedFile().getAbsolutePath());
            } else {
                readFileScoreCandidateDgnl(fc.getSelectedFile().getAbsolutePath());
            }
            doSearch();
        }
    }

    public static void readFileScoreCandidate(String filePath) {
        // 1. Mở file Excel
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = WorkbookFactory.create(fis);

            // 2. Lấy Sheet đầu tiên (index 0)
            Sheet sheet = workbook.getSheetAt(0);

            int sbdColIndex = 1; // Assuming CCCD is in the second column (index 1)

            // 3. Duyệt qua từng dòng (Row)
            for (Row row : sheet) {
                // Bỏ qua dòng tiêu đề (nếu có)
                if (row.getRowNum() == 0) {
                    continue;
                }
                // 4. Tạo đối tượng CandidateScore từ dữ liệu trong dòng
                DiemThiSinh candidatesScore = new DiemThiSinh();

                if (DiemThiSinhDAO.getCandidateScoreByCCCD(row.getCell(1).getStringCellValue()) != null) {
                    DiemThiSinh existingScore = DiemThiSinhDAO
                            .getCandidateScoreByCCCD(row.getCell(1).getStringCellValue());
                    ThiSinh candidate = ThiSinhDAO.getCandidateByCCCD(row.getCell(1).getStringCellValue());
                    candidatesScore.setIddiemthi(existingScore.getIddiemthi());
                    candidatesScore.setSobaodanh(candidate.getSobaodanh());

                    candidatesScore = existingScore; // Preserve existing scores for fields not in the Excel, to avoid
                                                     // overwriting with nulls
                }

                candidatesScore.setCccd(row.getCell(1).getStringCellValue());
                candidatesScore.setSobaodanh("00000000" + sbdColIndex++);
                candidatesScore.setD_phuongthuc("");
                candidatesScore.setTo(BigDecimal.valueOf(row.getCell(7).getNumericCellValue()));
                candidatesScore.setVa(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                candidatesScore.setLi(BigDecimal.valueOf(row.getCell(9).getNumericCellValue()));
                candidatesScore.setHo(BigDecimal.valueOf(row.getCell(10).getNumericCellValue()));
                candidatesScore.setSi(BigDecimal.valueOf(row.getCell(11).getNumericCellValue()));
                candidatesScore.setSu(BigDecimal.valueOf(row.getCell(12).getNumericCellValue()));
                candidatesScore.setDi(BigDecimal.valueOf(row.getCell(13).getNumericCellValue()));
                candidatesScore.setN1_thi(BigDecimal.valueOf(0.0));
                candidatesScore.setN1_cc(BigDecimal.valueOf(0.0));
                candidatesScore.setCncn(BigDecimal.valueOf(row.getCell(19).getNumericCellValue()));
                candidatesScore.setCnnn(BigDecimal.valueOf(row.getCell(20).getNumericCellValue()));
                candidatesScore.setTi(BigDecimal.valueOf(row.getCell(18).getNumericCellValue()));
                candidatesScore.setKtpl(BigDecimal.valueOf(row.getCell(17).getNumericCellValue()));
                candidatesScore.setNk1(BigDecimal.valueOf(row.getCell(22).getNumericCellValue()));
                candidatesScore.setNk2(BigDecimal.valueOf(row.getCell(23).getNumericCellValue()));

                DiemThiSinhDAO.createCandidateScore(candidatesScore);
            }

            workbook.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi đọc file Excel!");
        }
    }

    public static void readFileScoreCandidateDgnl(String filePath) {
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
                DiemThiSinh candidatesScore = new DiemThiSinh();

                if (DiemThiSinhDAO.getCandidateScoreByCCCD(row.getCell(1).getStringCellValue()) != null) {
                    DiemThiSinh existingScore = DiemThiSinhDAO
                            .getCandidateScoreByCCCD(row.getCell(1).getStringCellValue());
                    ThiSinh candidate = ThiSinhDAO.getCandidateByCCCD(row.getCell(1).getStringCellValue());
                    candidatesScore.setIddiemthi(existingScore.getIddiemthi());
                    candidatesScore.setSobaodanh(candidate.getSobaodanh());

                    candidatesScore = existingScore;
                }

                candidatesScore.setCccd(row.getCell(1).getStringCellValue());
                switch (row.getCell(6).getStringCellValue()) {
                    case "M1":
                        candidatesScore.setTO_NL(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "M2":
                        candidatesScore.setLI_NL(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "M3":
                        candidatesScore.setHO_NL(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "M4":
                        candidatesScore.setVA_NL(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "M5":
                        candidatesScore.setSI_NL(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "M6":
                        candidatesScore.setSU_NL(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "M7":
                        candidatesScore.setDI_NL(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "M8":
                        candidatesScore.setN1_NL(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "TO_VS":
                        candidatesScore.setTO_VS(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "LI_VS":
                        candidatesScore.setLI_VS(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "HO_VS":
                        candidatesScore.setHO_VS(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "VA_VS":
                        candidatesScore.setVA_VS(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "SI_VS":
                        candidatesScore.setSI_VS(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "SU_VS":
                        candidatesScore.setSU_VS(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "DI_VS":
                        candidatesScore.setDI_VS(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "N1_VS":
                        candidatesScore.setN1_VS(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    default:
                        break;
                }

                DiemThiSinhDAO.createCandidateScore(candidatesScore);
            }

            Sheet sheet1 = workbook.getSheetAt(1);

            for (Row row : sheet1) {
                // Bỏ qua dòng tiêu đề (nếu có)
                if (row.getRowNum() == 0) {
                    continue;
                }

                DiemThiSinh candidatesScore = new DiemThiSinh();

                if (DiemThiSinhDAO.getCandidateScoreByCCCD(row.getCell(1).getStringCellValue()) != null) {
                    DiemThiSinh existingScore = DiemThiSinhDAO
                            .getCandidateScoreByCCCD(row.getCell(1).getStringCellValue());
                    ThiSinh candidate = ThiSinhDAO.getCandidateByCCCD(row.getCell(1).getStringCellValue());
                    candidatesScore.setIddiemthi(existingScore.getIddiemthi());
                    candidatesScore.setSobaodanh(candidate.getSobaodanh());

                    candidatesScore = existingScore;
                }

                candidatesScore.setCccd(row.getCell(1).getStringCellValue());
                switch (row.getCell(3).getStringCellValue()) {
                    case "1":
                        candidatesScore.setNl1(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    case "2":
                        candidatesScore.setNl2(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        break;
                    default:
                        break;
                }
                DiemThiSinhDAO.createCandidateScore(candidatesScore);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi đọc file Excel!");
        }
    }

}
