package ui.panels;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import ui.dialogs.ThiSinhDialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class ThiSinhPanel extends BasePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cboKhuVuc, cboGioiTinh;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;

    private static final String[] COLUMNS = {
        "ID", "CCCD", "Số báo danh", "Họ", "Tên",
        "Ngày sinh", "Giới tính", "Khu vực", "Đối tượng",
        "Email", "Nơi sinh", "Điện thoại", "Hành động"
    };

    // Sample data matching xt_thisinhxettuyen25 schema
    private static final Object[][] SAMPLE_DATA = {
        {1, "001207004846", "001207004846", "Nguyễn Thị", "An",     "25/07/2007", "Nữ",  "KV1",    "",    "an@email.com",     "An Giang",   "0901234567"},
        {2, "001207005157", "001207005157", "Trần Thị",   "Bình",   "08/09/2007", "Nữ",  "KV1",    "",    "binh@email.com",   "Trà Vinh",   "0902345678"},
        {3, "001207006913", "001207006913", "Lê Văn",     "Cường",  "02/10/2006", "Nam", "KV3",    "",    "cuong@email.com",  "Hà Nội",     "0903456789"},
        {4, "001207006593", "001207006593", "Phạm Thị",   "Dung",   "02/08/2007", "Nữ",  "KV2",    "",    "dung@email.com",   "Trà Vinh",   "0904567890"},
        {5, "001207008830", "001207008830", "Hoàng Văn",  "Em",     "27/01/2007", "Nam", "KV1",    "UT3", "em@email.com",     "Sóc Trăng",  "0905678901"},
        {6, "001207009704", "001207009704", "Vũ Thị",     "Phương", "16/07/2007", "Nữ",  "KV2-NT", "",    "phuong@email.com", "Bạc Liêu",   "0906789012"},
        {7, "001207011459", "001207011459", "Đặng Thị",   "Giang",  "14/04/2007", "Nữ",  "KV3",    "",    "giang@email.com",  "TP HCM",     "0907890123"},
        {8, "001207012341", "001207012341", "Bùi Văn",    "Hùng",   "11/03/2007", "Nam", "KV2",    "",    "hung@email.com",   "Đồng Tháp",  "0908901234"},
        {9, "001207012439", "001207012439", "Ngô Thị",    "Lan",    "05/06/2007", "Nữ",  "KV1",    "UT1", "lan@email.com",    "Cà Mau",     "0909012345"},
        {10,"001207012684", "001207012684", "Đinh Văn",   "Minh",   "19/11/2007", "Nam", "KV3",    "",    "minh@email.com",   "TP HCM",     "0910123456"},
        {11,"001207013001", "001207013001", "Trương Thị", "Ngọc",   "03/02/2007", "Nữ",  "KV2",    "",    "ngoc@email.com",   "Vĩnh Long",  "0911234567"},
        {12,"001207013245", "001207013245", "Phan Văn",   "Ổn",     "21/05/2007", "Nam", "KV1",    "UT2", "on@email.com",     "Kiên Giang", "0912345678"},
    };

    public ThiSinhPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm thí sinh");
        btnImport.addActionListener(e -> showImportDialog());
        btnAdd.addActionListener(e -> new ThiSinhDialog(mainFrame, null).setVisible(true));

        // Search bar
        txtSearch   = UIComponents.searchField("Tìm CCCD, số báo danh, họ tên...");
        txtSearch.setPreferredSize(new Dimension(260, 30));
        txtSearch.addActionListener(e -> doSearch());

        cboKhuVuc   = UIComponents.comboBox("Tất cả khu vực", "KV1", "KV2", "KV2-NT", "KV3");
        cboGioiTinh = UIComponents.comboBox("Tất cả giới tính", "Nam", "Nữ");

        RoundButton btnSearch = RoundButton.secondary("Tìm kiếm");
        btnSearch.addActionListener(e -> doSearch());

        JPanel searchBar = buildSearchBar(
            new JLabel("  Tìm kiếm: "), txtSearch,
            new JLabel("  Khu vực: "), cboKhuVuc,
            new JLabel("  Giới tính: "), cboGioiTinh,
            btnSearch
        );
        add(searchBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData(SAMPLE_DATA);

        // Set column widths
        int[] widths = {55, 110, 110, 100, 80, 85, 65, 70, 55, 140, 90, 95, 80};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Action column renderer/editor
        table.getColumn("Hành động").setCellRenderer(new ActionRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COLUMNS.length - 1 && row >= 0) {
                    handleRowAction(row, e.getX(), e.getY());
                }
            }
        });

        JPanel center = new JPanel(new BorderLayout());
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        // Pagination
        int total = SAMPLE_DATA.length;
        int pages = (int) Math.ceil((double) total / PAGE_SIZE);
        center.add(buildPagination(currentPage, pages, total, null), BorderLayout.SOUTH);

        // Wrap with border
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(buildTopBar(
            "Thí sinh",
            "Thống kê và danh sách thí sinh đăng ký xét tuyển",
            btnImport, btnAdd
        ), BorderLayout.NORTH);

        JPanel statRow = new JPanel(new GridLayout(1, 4, 10, 0));
        statRow.setOpaque(false);
        statRow.setBorder(new EmptyBorder(10, 14, 8, 14));
        statRow.add(UIComponents.statCard("Tổng thí sinh", "2,412", AppTheme.PRIMARY, "Tất cả hồ sơ"));
        statRow.add(UIComponents.statCard("Theo đối tượng", "624", AppTheme.AMBER, "UT1/UT2/UT3"));
        statRow.add(UIComponents.statCard("Khu vực 1", "812", AppTheme.GREEN, "KV1"));
        statRow.add(UIComponents.statCard("Khu vực 2", "1,125", AppTheme.RED, "KV2/KV2-NT"));

        JPanel topBody = new JPanel(new BorderLayout());
        topBody.add(statRow, BorderLayout.NORTH);
        topBody.add(searchBar, BorderLayout.SOUTH);
        topSection.add(topBody, BorderLayout.CENTER);

        removeAll();
        setLayout(new BorderLayout());
        add(topSection, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void loadData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            Object[] rowWithBtn = new Object[COLUMNS.length];
            System.arraycopy(row, 0, rowWithBtn, 0, Math.min(row.length, COLUMNS.length - 1));
            rowWithBtn[COLUMNS.length - 1] = "actions";
            tableModel.addRow(rowWithBtn);
        }
    }

    private void doSearch() {
        // TODO: integrate with DAO/Hibernate query
        JOptionPane.showMessageDialog(this,
            "Tìm kiếm: \"" + txtSearch.getText() + "\"\n" +
            "Khu vực: " + cboKhuVuc.getSelectedItem() + "\n" +
            "Giới tính: " + cboGioiTinh.getSelectedItem() + "\n\n" +
            "(Demo) Sẽ lọc theo các điều kiện trên.",
            "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleRowAction(int row, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("🔍 Xem chi tiết");
        JMenuItem editItem = new JMenuItem("✏ Sửa thông tin");
        JMenuItem deleteItem = new JMenuItem("🗑 Xóa");
        viewItem.addActionListener(e -> showDetailDialog(row));
        editItem.addActionListener(e -> {
            Object cccd = tableModel.getValueAt(row, 1);
            new ThiSinhDialog(mainFrame, cccd.toString()).setVisible(true);
        });
        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa thí sinh: " + tableModel.getValueAt(row, 3) + " " + tableModel.getValueAt(row, 4) + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(row);
            }
        });
        menu.add(viewItem);
        menu.add(editItem);
        menu.addSeparator();
        menu.add(deleteItem);
        menu.show(table, x, y);
    }

    private void showDetailDialog(int row) {
        String cccd = tableModel.getValueAt(row, 1).toString();
        String fullName = tableModel.getValueAt(row, 3) + " " + tableModel.getValueAt(row, 4);

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Chi tiết thí sinh", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(560, 420);
        d.setLocationRelativeTo(this);

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 14, 10, 14));
        header.setBackground(AppTheme.BG_PRIMARY);
        JLabel title = new JLabel(fullName + " — " + cccd);
        title.setFont(AppTheme.FONT_BOLD);
        JLabel sub = new JLabel("Xem thông tin và điểm của thí sinh (demo)");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_SECOND);
        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY);

        JPanel tabTHPT = new JPanel(new GridLayout(4, 2, 8, 8));
        tabTHPT.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabTHPT.setBackground(AppTheme.BG_PRIMARY);
        tabTHPT.add(new JLabel("Toán:")); tabTHPT.add(new JLabel("8.25"));
        tabTHPT.add(new JLabel("Ngữ văn:")); tabTHPT.add(new JLabel("7.50"));
        tabTHPT.add(new JLabel("Tiếng Anh:")); tabTHPT.add(new JLabel("8.10"));
        tabTHPT.add(new JLabel("Tổ hợp:")); tabTHPT.add(new JLabel("D01"));

        JPanel tabDGNL = new JPanel(new GridLayout(3, 2, 8, 8));
        tabDGNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabDGNL.setBackground(AppTheme.BG_PRIMARY);
        tabDGNL.add(new JLabel("Điểm ĐGNL:")); tabDGNL.add(new JLabel("920"));
        tabDGNL.add(new JLabel("Quy đổi thang 30:")); tabDGNL.add(new JLabel("24.60"));
        tabDGNL.add(new JLabel("Ghi chú:")); tabDGNL.add(new JLabel("—"));

        JPanel tabVSAT = new JPanel(new GridLayout(3, 2, 8, 8));
        tabVSAT.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabVSAT.setBackground(AppTheme.BG_PRIMARY);
        tabVSAT.add(new JLabel("Điểm V-SAT:")); tabVSAT.add(new JLabel("780"));
        tabVSAT.add(new JLabel("Quy đổi thang 30:")); tabVSAT.add(new JLabel("23.40"));
        tabVSAT.add(new JLabel("Ghi chú:")); tabVSAT.add(new JLabel("Nếu có"));

        tabs.addTab("Điểm THPT", tabTHPT);
        tabs.addTab("Điểm ĐGNL", tabDGNL);
        tabs.addTab("Điểm V-SAT", tabVSAT);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        RoundButton close = RoundButton.secondary("Đóng");
        close.addActionListener(e -> d.dispose());
        footer.add(close);

        d.setLayout(new BorderLayout());
        d.add(header, BorderLayout.NORTH);
        d.add(tabs, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showImportDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file Excel — Thí sinh");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx, *.xls)", "xlsx", "xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                "File: " + fc.getSelectedFile().getName() + "\n\n" +
            "(Demo) Sẽ import và kiểm tra dữ liệu trước khi lưu.",
            "Import thí sinh", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Button renderer for action column
    static class ActionRenderer extends DefaultTableCellRenderer {
        private JPanel panel;
        private JButton btnEdit, btnDel;

        public ActionRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 2));
            panel.setOpaque(true);
            btnEdit = new JButton("Sửa");
            btnEdit.setFont(AppTheme.FONT_SMALL);
            btnEdit.setBackground(AppTheme.BG_SECONDARY);
            btnEdit.setForeground(AppTheme.TEXT_PRIMARY);
            btnEdit.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
            btnEdit.setFocusPainted(false);

            btnDel = new JButton("Xóa");
            btnDel.setFont(AppTheme.FONT_SMALL);
            btnDel.setBackground(AppTheme.RED_LIGHT);
            btnDel.setForeground(AppTheme.RED);
            btnDel.setBorder(BorderFactory.createLineBorder(AppTheme.RED_LIGHT));
            btnDel.setFocusPainted(false);

            panel.add(btnEdit);
            panel.add(btnDel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            panel.setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
            return panel;
        }
    }
}
