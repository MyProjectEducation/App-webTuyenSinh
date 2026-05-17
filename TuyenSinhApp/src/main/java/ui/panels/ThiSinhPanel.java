package ui.panels;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import ui.dialogs.ThiSinhDialog;
import ui.icon.DrawHamburger;
import dao.ThiSinhDAO;
import entity.ThiSinh;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.File;
import org.apache.poi.ss.usermodel.*;

public class ThiSinhPanel extends BasePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cboKhuVuc, cboGioiTinh;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private JPanel center = new JPanel(new BorderLayout());

    // Maintain a master list and a filtered list for proper searching/pagination
    private List<ThiSinh> ALL_DATA = ThiSinhDAO.getAllCandidates();
    private List<ThiSinh> filteredData = new ArrayList<>(ALL_DATA);

    private static final String[] COLUMNS = {
            "ID", "CCCD", "Số báo danh", "Họ", "Tên",
            "Ngày sinh", "Giới tính", "Khu vực", "Đối tượng",
            "Email", "Nơi sinh", "Điện thoại", "Hành động"
    };

    public ThiSinhPanel(MainFrame mainFrame) {
        super(mainFrame);
        ALL_DATA.sort((a, b) -> Integer.compare(a.getIdthisinh(), b.getIdthisinh()));
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd = RoundButton.primary("+ Thêm thí sinh");
        btnImport.addActionListener(e -> showImportDialog());
        btnAdd.addActionListener(e -> new ThiSinhDialog(mainFrame, null).setVisible(true));

        // Search bar setup
        txtSearch = UIComponents.searchField("Tìm CCCD, số báo danh, họ tên...");
        txtSearch.setPreferredSize(new Dimension(260, 30));
        txtSearch.addActionListener(e -> doSearch());

        cboKhuVuc = UIComponents.comboBox("Tất cả khu vực", "1", "2", "2NT", "3");
        cboGioiTinh = UIComponents.comboBox("Tất cả giới tính", "Nam", "Nữ");

        RoundButton btnSearch = RoundButton.secondary("Tìm kiếm");
        btnSearch.addActionListener(e -> doSearch());

        JPanel searchBar = buildSearchBar(
                new JLabel("  Tìm kiếm: "), txtSearch,
                new JLabel("  Khu vực: "), cboKhuVuc,
                new JLabel("  Giới tính: "), cboGioiTinh,
                btnSearch);

        // Table initialization
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);

        // Column width optimization
        int[] widths = { 55, 110, 110, 100, 80, 85, 65, 70, 55, 140, 90, 95, 80 };
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

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

        center.add(new JScrollPane(table), BorderLayout.CENTER);

        // Initial setup and pagination assembly
        updatePaginationAndTable();

        // Layout assemblies
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(buildTopBar(
                "Thí sinh",
                "Thống kê và danh sách thí sinh đăng ký xét tuyển",
                btnImport, btnAdd), BorderLayout.NORTH);

        JPanel statRow = new JPanel(new GridLayout(1, 4, 10, 0));
        statRow.setOpaque(false);
        statRow.setBorder(new EmptyBorder(10, 14, 8, 14));
        statRow.add(UIComponents.statCard("Tổng thí sinh", String.valueOf(ALL_DATA.size()), AppTheme.PRIMARY,
                "Tất cả hồ sơ"));

        int stdt = 0;
        int stkv1 = 0;
        int stkv2 = 0;

        for (ThiSinh ts : ALL_DATA) {
            if (ts.getDoiTuong() != null && ts.getDoiTuong().startsWith("UT"))
                stdt++;
            if ("KV1".equals(ts.getKhuVuc()) || "1".equals(ts.getKhuVuc()))
                stkv1++;
            if ("KV2".equals(ts.getKhuVuc()) || "KV2-NT".equals(ts.getKhuVuc()) || "2".equals(ts.getKhuVuc())
                    || "2-NT".equals(ts.getKhuVuc()) || "2NT".equals(ts.getKhuVuc()))
                stkv2++;
        }
        statRow.add(UIComponents.statCard("Theo đối tượng", String.valueOf(stdt), AppTheme.AMBER, "UT1/UT2/UT3"));
        statRow.add(UIComponents.statCard("Khu vực 1", String.valueOf(stkv1), AppTheme.GREEN, "KV1"));
        statRow.add(UIComponents.statCard("Khu vực 2", String.valueOf(stkv2), AppTheme.RED, "KV2/KV2-NT"));

        JPanel topBody = new JPanel(new BorderLayout());
        topBody.add(statRow, BorderLayout.NORTH);
        topBody.add(searchBar, BorderLayout.SOUTH);
        topSection.add(topBody, BorderLayout.CENTER);

        removeAll();
        setLayout(new BorderLayout());
        add(topSection, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void loadData(List<ThiSinh> data) {
        tableModel.setRowCount(0);
        for (ThiSinh candidate : data) {
            Object[] row = {
                    candidate.getIdthisinh(),
                    candidate.getCccd(),
                    candidate.getSobaodanh(),
                    candidate.getHo(),
                    candidate.getTen(),
                    candidate.getNgaySinh(),
                    candidate.getGioiTinh(),
                    candidate.getKhuVuc(),
                    candidate.getDoiTuong(),
                    candidate.getEmail(),
                    candidate.getNoiSinh(),
                    candidate.getDienThoai()
            };
            Object[] rowWithBtn = new Object[COLUMNS.length];
            System.arraycopy(row, 0, rowWithBtn, 0, Math.min(row.length, COLUMNS.length - 1));
            rowWithBtn[COLUMNS.length - 1] = "actions";
            tableModel.addRow(rowWithBtn);
        }
    }

    private void doSearch() {
        ALL_DATA = ThiSinhDAO.getAllCandidates();
        String query = txtSearch.getText().trim().toLowerCase();
        String targetKV = (String) cboKhuVuc.getSelectedItem();
        String targetGT = (String) cboGioiTinh.getSelectedItem();

        filteredData = new ArrayList<>();
        for (ThiSinh ts : ALL_DATA) {
            boolean matchesQuery = query.isEmpty() ||
                    (ts.getCccd() != null && ts.getCccd().toLowerCase().contains(query)) ||
                    (ts.getSobaodanh() != null && ts.getSobaodanh().toLowerCase().contains(query)) ||
                    ((ts.getHo() + " " + ts.getTen()).toLowerCase().contains(query));

            boolean matchesKV = "Tất cả khu vực".equals(targetKV)
                    || (ts.getKhuVuc() != null && ts.getKhuVuc().equals(targetKV));
            boolean matchesGT = "Tất cả giới tính".equals(targetGT)
                    || (ts.getGioiTinh() != null && ts.getGioiTinh().equalsIgnoreCase(targetGT));

            if (matchesQuery && matchesKV && matchesGT) {
                filteredData.add(ts);
            }
        }

        currentPage = 1; // Reset to page 1 on active search execution
        updatePaginationAndTable();
    }

    private void updatePaginationAndTable() {
        int total = filteredData.size();
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        if (totalPages == 0)
            totalPages = 1;

        loadData(getPageData(filteredData, currentPage, PAGE_SIZE));

        // Safe component removal clean up for South container
        BorderLayout layout = (BorderLayout) center.getLayout();
        Component currentSouth = layout.getLayoutComponent(BorderLayout.SOUTH);
        if (currentSouth != null) {
            center.remove(currentSouth);
        }

        // Add the fresh re-rendered layout components back to context container
        center.add(buildPagination(currentPage, totalPages, total, getPageActions(totalPages)), BorderLayout.SOUTH);
        center.revalidate();
        center.repaint();
    }

    private void handleRowAction(int row, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("🔍 Xem chi tiết");
        JMenuItem editItem = new JMenuItem("✏ Sửa thông tin");
        JMenuItem deleteItem = new JMenuItem("🗑 Xóa");

        viewItem.addActionListener(e -> {
            Object id = tableModel.getValueAt(row, 0);
            new ThiSinhDialog(mainFrame, Integer.parseInt(id.toString())).setVisible(true);
        });
        editItem.addActionListener(e -> {
            Object cccd = tableModel.getValueAt(row, 1);
            new ThiSinhDialog(mainFrame, cccd.toString()).setVisible(true);
            ALL_DATA = ThiSinhDAO.getAllCandidates();
            doSearch();
        });

        deleteItem.addActionListener(e -> {
            String fullName = tableModel.getValueAt(row, 3) + " " + tableModel.getValueAt(row, 4);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa thí sinh: " + fullName + "?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {

                Object idObj = tableModel.getValueAt(row, 0);
                if (idObj instanceof Integer) {
                    int targetId = (Integer) idObj;
                    ALL_DATA.removeIf(ts -> ts.getIdthisinh() == targetId);
                    filteredData.removeIf(ts -> ts.getIdthisinh() == targetId);

                    ThiSinhDAO.deleteCandidate(ThiSinhDAO.getCandidateById(targetId));
                }
                updatePaginationAndTable();
                ALL_DATA = ThiSinhDAO.getAllCandidates();
                doSearch();
            }
        });

        menu.add(viewItem);
        menu.add(editItem);
        menu.addSeparator();
        menu.add(deleteItem);
        menu.show(table, x, y);
    }

    private void showImportDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file Excel — Thí sinh");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx, *.xls)", "xlsx", "xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            ThiSinhPanel.readFileCandidate(file.getAbsolutePath());
            ALL_DATA = ThiSinhDAO.getAllCandidates();
            doSearch();
        }
    }

    // Custom Button renderer class definition for table items context configuration
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

    // Clean zero-indexed based offset implementation safely bound checking logic
    // parameters
    private List<ThiSinh> getPageData(List<ThiSinh> fullData, int page, int pageSize) {
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= fullData.size() || fromIndex < 0) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + pageSize, fullData.size());
        return fullData.subList(fromIndex, toIndex);
    }

    private Runnable[] getPageActions(int totalPages) {
        Runnable[] actions = new Runnable[totalPages];
        for (int i = 0; i < totalPages; i++) {
            final int page = i + 1;
            actions[i] = () -> {
                currentPage = page;
                updatePaginationAndTable();
            };
        }
        return actions;
    }

    public static void readFileCandidate(String filePath) {
        try {
            // 1. Mở file Excel
            FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = WorkbookFactory.create(fis);

            // 2. Lấy Sheet đầu tiên (index 0)
            Sheet sheet = workbook.getSheetAt(0);

            int sbdCounter = 1; // Counter for generating unique SBD
            // 3. Duyệt qua từng dòng (Row)
            for (Row row : sheet) {

                // Bỏ qua dòng tiêu đề (nếu có)
                if (row.getRowNum() == 0) {
                    continue;
                }
                // 4. Tạo đối tượng Candidate từ dữ liệu trong dòng
                ThiSinh candidate = new ThiSinh();
                if (ThiSinhDAO.getCandidateByCCCD(row.getCell(1).getStringCellValue()) != null) {
                    ThiSinh existingCandidate = ThiSinhDAO.getCandidateByCCCD(row.getCell(1).getStringCellValue());
                    candidate.setIdthisinh(existingCandidate.getIdthisinh());
                }

                candidate.setCccd(row.getCell(1).getStringCellValue());
                candidate.setSobaodanh("0000000" + sbdCounter++);
                candidate.setHo(row.getCell(2).getStringCellValue());
                candidate.setTen(row.getCell(2).getStringCellValue());
                candidate.setNgaySinh(row.getCell(3).getStringCellValue());
                candidate.setDienThoai("00000000");
                candidate.setPassword("123456");
                candidate.setGioiTinh(row.getCell(4).getStringCellValue());
                candidate.setEmail("0@gmail.com");
                candidate.setNoiSinh(row.getCell(35).getStringCellValue());
                candidate.setUpdatedAt(new Date());
                candidate.setDoiTuong(row.getCell(5).getStringCellValue());
                candidate.setKhuVuc(row.getCell(6).getStringCellValue());

                ThiSinhDAO.createCandidate(candidate);
            }

            workbook.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi đọc file Excel!");
        }
    }
}