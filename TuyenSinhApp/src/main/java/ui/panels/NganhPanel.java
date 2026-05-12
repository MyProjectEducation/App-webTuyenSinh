package ui.panels;

import ui.MainFrame;
import dao.NganhDAO;
import dao.NganhTohopDAO;
import entity.Nganh;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import ui.dialogs.NganhDialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private final NganhDAO nganhDAO = new NganhDAO();
    private final NganhTohopDAO nganhTohopDAO = new NganhTohopDAO();
    private List<Nganh> cachedData = new ArrayList<>();

    public NganhPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm ngành");
        btnImport.addActionListener(e -> showImportDialog());
        btnAdd.addActionListener(e -> new NganhDialog(mainFrame, null, this::reloadData).setVisible(true));

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
        reloadData();

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

    private void reloadData() {
        try {
            cachedData = nganhDAO.findAll();
            fillTable(cachedData);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không thể tải dữ liệu ngành. Kiểm tra kết nối DB.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillTable(List<Nganh> data) {
        tableModel.setRowCount(0);
        for (Nganh nganh : data) {
            Object[] r = new Object[COLUMNS.length];
            r[0] = nganh.getId();
            r[1] = nganh.getMaNganh();
            r[2] = nganh.getTenNganh();
            r[3] = nganh.getToHopGoc();
            r[4] = nganh.getChiTieu();
            r[5] = nganh.getDiemSan();
            r[6] = nganh.getDiemTrungTuyen();
            r[7] = buildPhuongThucText(nganh);
            r[8] = 0;
            r[9] = "actions";
            tableModel.addRow(r);
        }
    }

    private String buildPhuongThucText(Nganh n) {
        List<String> parts = new ArrayList<>();
        if ("1".equals(n.getThpt())) parts.add("THPT");
        if ("1".equals(n.getVsat())) parts.add("V-SAT");
        if ("1".equals(n.getDgnl())) parts.add("ĐGNL");
        if ("1".equals(n.getTuyenThang())) parts.add("Tuyển thẳng");
        return parts.isEmpty() ? "—" : String.join(", ", parts);
    }

    private void doSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String method = String.valueOf(cboPhuongThuc.getSelectedItem());

        List<Nganh> filtered = cachedData.stream()
            .filter(n -> keyword.isEmpty()
                || (n.getMaNganh() != null && n.getMaNganh().toLowerCase().contains(keyword))
                || (n.getTenNganh() != null && n.getTenNganh().toLowerCase().contains(keyword)))
            .filter(n -> "Tất cả phương thức".equals(method)
                || ("THPT".equals(method) && "1".equals(n.getThpt()))
                || ("V-SAT".equals(method) && "1".equals(n.getVsat()))
                || ("ĐGNL".equals(method) && "1".equals(n.getDgnl())))
            .collect(Collectors.toList());

        fillTable(filtered);
    }

    private void handleAction(int row, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa ngành");
        JMenuItem del  = new JMenuItem("🗑 Xóa ngành");
        edit.addActionListener(ev -> new NganhDialog(mainFrame,
            tableModel.getValueAt(row, 1).toString(),
            this::reloadData).setVisible(true));
        del.addActionListener(ev -> {
            int c = JOptionPane.showConfirmDialog(this, "Xóa ngành: " + tableModel.getValueAt(row, 2) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                try {
                    String maNganh = String.valueOf(tableModel.getValueAt(row, 1));
                    long usedByNganhTohop = nganhTohopDAO.countByMaNganh(maNganh);
                    if (usedByNganhTohop > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Không thể xóa ngành " + maNganh + ". "
                                + "Đang được dùng bởi Ngành-Tổ hợp (" + usedByNganhTohop + ").\n"
                                + "Vui lòng xóa các liên quan trước.",
                            "Ràng buộc dữ liệu", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Integer id = Integer.valueOf(tableModel.getValueAt(row, 0).toString());
                    nganhDAO.deleteById(id);
                    reloadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa ngành. Kiểm tra ràng buộc DB.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
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
