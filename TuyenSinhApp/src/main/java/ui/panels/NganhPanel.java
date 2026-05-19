package ui.panels;

import ui.MainFrame;
import dao.NganhDAO;
import dao.NganhTohopDAO;
import entity.Nganh;
import entity.NguyenVong;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import ui.dialogs.NganhDialog;
import util.ExcelSmartUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
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

    private Map<String, Long> getAspirationCounts() {
        Map<String, Long> map = new HashMap<>();
        try (org.hibernate.Session session = util.HibernateUtil.getSessionFactory().openSession()) {
            List<?> list = session.createQuery(
                "select nv.nvManganh, count(nv) from NguyenVong nv group by nv.nvManganh")
                .list();
            for (Object obj : list) {
                if (obj instanceof Object[]) {
                    Object[] row = (Object[]) obj;
                    if (row[0] != null) {
                        map.put(String.valueOf(row[0]).trim(), (Long) row[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void fillTable(List<Nganh> data) {
        tableModel.setRowCount(0);
        Map<String, Long> countMap = getAspirationCounts();
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
            r[8] = countMap.getOrDefault(nganh.getMaNganh(), 0L);
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
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx, *.xls)", "xlsx", "xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            importFromExcel(fc.getSelectedFile());
        }
    }

    private void importFromExcel(File file) {
        try {
            Map<String, String> mapping = new HashMap<>();
            mapping.put("mactdt", "manganh");
            mapping.put("maxettuyen", "manganh");
            mapping.put("manganh", "manganh");
            mapping.put("manganhdaotao", "manganh");
            mapping.put("machuongtrinhdaotao", "manganh");
            mapping.put("tenctdt", "tennganh");
            mapping.put("tennganh", "tennganh");
            mapping.put("tennganhchuan", "tennganh");
            mapping.put("tohopgoc", "n_tohopgoc");
            mapping.put("tentohop", "tohopmon");
            mapping.put("matohop", "tohopmon");
            mapping.put("tohopmon", "tohopmon");
            mapping.put("tohop", "tohopmon");
            mapping.put("goc", "is_goc");
            mapping.put("chitieuchot", "n_chitieu");
            mapping.put("chitieu", "n_chitieu");
            mapping.put("nguongdauvao", "n_diemsan");
            mapping.put("diemsan", "n_diemsan");
            mapping.put("diemchuan", "n_diemtrungtuyen");
            mapping.put("diemtrungtuyen", "n_diemtrungtuyen");
            mapping.put("tuyenthang", "n_tuyenthang");
            mapping.put("xettuyenthang", "n_tuyenthang");
            mapping.put("dgnl", "n_dgnl");
            mapping.put("diemdgnl", "n_dgnl");
            mapping.put("thpt", "n_thpt");
            mapping.put("diemthpt", "n_thpt");
            mapping.put("vsat", "n_vsat");
            mapping.put("diemvsat", "n_vsat");
            mapping.put("sltuyenthang", "sl_xtt");
            mapping.put("slxtt", "sl_xtt");
            mapping.put("sldgnl", "sl_dgnl");
            mapping.put("slvsat", "sl_vsat");
            mapping.put("slthpt", "sl_thpt");

            List<Map<String, String>> rows = ExcelSmartUtils.smartScan(file, mapping);
            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu hợp lệ trong file Excel.",
                    "Import ngành", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int success = 0;
            for (Map<String, String> data : rows) {
                if (data.containsKey("tohopmon")) {
                    boolean isGoc = isTruthy(data.get("is_goc"));
                    if (isGoc) {
                        data.put("n_tohopgoc", data.get("tohopmon"));
                    }
                }

                String ma = val(data, "manganh");
                if (ma.isEmpty()) continue;

                Nganh nganh = nganhDAO.findByMaNganh(ma);
                boolean isUpdate = nganh != null;
                if (nganh == null) nganh = new Nganh();

                nganh.setMaNganh(ma);
                if (!isUpdate) {
                    nganh.setTenNganh(val(data, "tennganh"));
                }
                if (hasValue(data, "tennganh")) nganh.setTenNganh(val(data, "tennganh"));
                if (hasValue(data, "n_tohopgoc")) nganh.setToHopGoc(val(data, "n_tohopgoc"));
                if (hasValue(data, "n_chitieu")) nganh.setChiTieu(parseInt(val(data, "n_chitieu"), 0));
                if (hasValue(data, "n_diemsan")) nganh.setDiemSan(parseDecimal(val(data, "n_diemsan")));
                if (hasValue(data, "n_diemtrungtuyen")) nganh.setDiemTrungTuyen(parseDecimal(val(data, "n_diemtrungtuyen")));
                if (hasValue(data, "n_tuyenthang")) nganh.setTuyenThang(parseFlag(val(data, "n_tuyenthang")));
                if (hasValue(data, "n_dgnl")) nganh.setDgnl(parseFlag(val(data, "n_dgnl")));
                if (hasValue(data, "n_thpt")) nganh.setThpt(parseFlag(val(data, "n_thpt")));
                if (hasValue(data, "n_vsat")) nganh.setVsat(parseFlag(val(data, "n_vsat")));
                if (hasValue(data, "sl_xtt")) nganh.setSlXtt(parseInt(val(data, "sl_xtt"), 0));
                if (hasValue(data, "sl_dgnl")) nganh.setSlDgnl(parseInt(val(data, "sl_dgnl"), 0));
                if (hasValue(data, "sl_vsat")) nganh.setSlVsat(parseInt(val(data, "sl_vsat"), 0));
                if (hasValue(data, "sl_thpt")) nganh.setSlThpt(val(data, "sl_thpt"));

                if (!isUpdate) {
                    if (nganh.getTenNganh() == null) nganh.setTenNganh("");
                    if (nganh.getTuyenThang() == null) nganh.setTuyenThang("0");
                    if (nganh.getDgnl() == null) nganh.setDgnl("0");
                    if (nganh.getThpt() == null) nganh.setThpt("0");
                    if (nganh.getVsat() == null) nganh.setVsat("0");
                    if (nganh.getChiTieu() == null) nganh.setChiTieu(0);
                    if (nganh.getSlXtt() == null) nganh.setSlXtt(0);
                    if (nganh.getSlDgnl() == null) nganh.setSlDgnl(0);
                    if (nganh.getSlVsat() == null) nganh.setSlVsat(0);
                    if (nganh.getSlThpt() == null) nganh.setSlThpt("0");
                }

                nganhDAO.saveOrUpdate(nganh);
                success++;
            }

            reloadData();
            JOptionPane.showMessageDialog(this,
                "Import thành công! Đã thêm/cập nhật " + success + " ngành.",
                "Import ngành", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không thể import Excel. Kiểm tra định dạng file.",
                "Import ngành", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String val(Map<String, String> data, String key) {
        String v = data.get(key);
        return v == null ? "" : v.trim();
    }

    private boolean hasValue(Map<String, String> data, String key) {
        String v = data.get(key);
        return v != null && !v.trim().isEmpty();
    }

    private Integer parseInt(String val, int fallback) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception ex) {
            return fallback;
        }
    }

    private BigDecimal parseDecimal(String val) {
        try {
            return new BigDecimal(val.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isTruthy(String val) {
        if (val == null) return false;
        String v = val.trim().toLowerCase();
        return v.equals("x") || v.equals("1") || v.equals("co") || v.equals("có")
            || v.equals("true") || v.equals("v") || v.equals("yes") || v.equals("y")
            || v.equals("+") || v.equals("goc") || v.equals("gốc");
    }

    private String parseFlag(String val) {
        return isTruthy(val) ? "1" : "0";
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
