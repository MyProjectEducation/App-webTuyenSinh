package ui.panels;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import ui.dialogs.NganhDialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class NganhPanel extends BasePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cboPhuongThuc;

    private static final String[] COLUMNS = {
        "ID", "Mã ngành", "Tên ngành", "Tổ hợp gốc",
        "Chỉ tiêu", "Điểm sàn", "Điểm trúng tuyển",
        "Phương thức xét tuyển", "Số NV đăng ký", "Hành động"
    };

    private static final Object[][] SAMPLE_DATA = {
        {1,  "7140114", "Quản lý giáo dục",    "D01", 40,  17.00, 22.50, "THPT, V-SAT, ĐGNL",  1860},
        {2,  "7140201", "Giáo dục Mầm non",     "M01", 200, 20.00, 24.00, "THPT",               3210},
        {3,  "7140202", "Giáo dục Tiểu học",    "C01", 200, 21.00, 25.00, "THPT, V-SAT",        2840},
        {4,  "7140205", "Giáo dục Chính trị",   "C01", 40,  23.00, 26.00, "THPT",               620},
        {5,  "7140209", "Sư phạm Toán học",     "A00", 40,  24.50, 28.00, "THPT, V-SAT, ĐGNL",  940},
        {6,  "7140211", "Sư phạm Vật lý",       "A00", 10,  24.00, 27.00, "THPT",               240},
        {7,  "7140212", "Sư phạm Hoá học",      "A00", 10,  24.00, 27.50, "THPT",               260},
        {8,  "7140213", "Sư phạm Sinh học",     "B00", 10,  23.00, 26.00, "THPT",               210},
        {9,  "7140217", "Sư phạm Ngữ văn",      "C00", 50,  24.00, 27.50, "THPT",               780},
        {10, "7140218", "Sư phạm Lịch sử",      "C00", 25,  25.00, 27.00, "THPT",               420},
        {11, "7140219", "Sư phạm Địa lý",       "C04", 25,  22.00, 25.00, "THPT",               390},
        {12, "7140221", "Sư phạm Âm nhạc",      "N00", 75,  18.00, 22.00, "THPT",               310},
        {13, "7140222", "Sư phạm Mỹ thuật",     "H00", 75,  18.00, 22.00, "THPT",               290},
        {14, "7140231", "Sư phạm Tiếng Anh",    "D01", 120, 24.00, 28.00, "THPT, V-SAT, ĐGNL",  1460},
        {15, "7140247", "SP Khoa học tự nhiên", "A00", 60,  22.00, 26.50, "THPT, V-SAT, ĐGNL",  880},
        {16, "7140249", "SP Lịch sử - Địa lý",  "C04", 40,  19.00, 23.50, "THPT",               510},
    };

    public NganhPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm ngành");
        btnImport.addActionListener(e -> showImportDialog());
        btnAdd.addActionListener(e -> new NganhDialog(mainFrame, null).setVisible(true));

        JPanel topBar = buildTopBar(
            "Ngành tuyển sinh",
            "Danh sách ngành, chỉ tiêu, điểm chuẩn và số lượng nguyện vọng đăng ký",
            btnImport, btnAdd
        );

        // Search bar
        txtSearch = UIComponents.searchField("Tìm mã ngành, tên ngành...");
        txtSearch.setPreferredSize(new Dimension(240, 30));
        cboPhuongThuc = UIComponents.comboBox("Tất cả phương thức", "THPT", "V-SAT", "ĐGNL");
        RoundButton btnSearch = RoundButton.secondary("Tìm");
        btnSearch.addActionListener(e -> doSearch());

        JPanel searchBar = buildSearchBar(
            new JLabel("  Tìm: "), txtSearch,
            new JLabel("  Phương thức: "), cboPhuongThuc,
            btnSearch
        );

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData();

        // Column widths
        int[] widths = {45, 85, 190, 80, 70, 80, 95, 150, 110, 90};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Center-align numeric columns
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 4; i < COLUMNS.length - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        // Action column
        table.getColumn("Hành động").setCellRenderer(new ActionRenderer());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COLUMNS.length - 1 && row >= 0) handleAction(row, e);
            }
        });

        JPanel center = new JPanel(new BorderLayout());
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(topBar, BorderLayout.NORTH);
        topSection.add(searchBar, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(topSection, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Object[] row : SAMPLE_DATA) {
            Object[] r = new Object[COLUMNS.length];
            System.arraycopy(row, 0, r, 0, Math.min(row.length, COLUMNS.length - 1));
            r[COLUMNS.length - 1] = "actions";
            tableModel.addRow(r);
        }
    }

    private void doSearch() {
        JOptionPane.showMessageDialog(this,
            "(Demo) Tìm theo mã ngành/tên ngành và phương thức đã chọn.",
            "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAction(int row, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa ngành");
        JMenuItem del  = new JMenuItem("🗑 Xóa ngành");
        edit.addActionListener(ev -> new NganhDialog(mainFrame, tableModel.getValueAt(row, 1).toString()).setVisible(true));
        del.addActionListener(ev -> {
            int c = JOptionPane.showConfirmDialog(this, "Xóa ngành: " + tableModel.getValueAt(row, 2) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) tableModel.removeRow(row);
        });
        menu.add(edit);
        menu.addSeparator();
        menu.add(del);
        menu.show(table, e.getX(), e.getY());
    }

    private void showImportDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import Excel — Ngành tuyển sinh");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx)", "xlsx", "xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "(Demo) Sẽ import danh sách ngành từ file đã chọn.",
                "Import ngành", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    static class ActionRenderer extends DefaultTableCellRenderer {
        private JPanel panel;
        public ActionRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 2));
            panel.setOpaque(true);
            JButton e = new JButton("Sửa"); e.setFont(AppTheme.FONT_SMALL);
            e.setBackground(AppTheme.BG_SECONDARY); e.setForeground(AppTheme.TEXT_PRIMARY);
            e.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER)); e.setFocusPainted(false);
            JButton d = new JButton("Xóa"); d.setFont(AppTheme.FONT_SMALL);
            d.setBackground(AppTheme.RED_LIGHT); d.setForeground(AppTheme.RED);
            d.setBorder(BorderFactory.createLineBorder(AppTheme.RED_LIGHT)); d.setFocusPainted(false);
            panel.add(e); panel.add(d);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            panel.setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
            return panel;
        }
    }
}
