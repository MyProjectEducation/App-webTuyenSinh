package ui.panels;

import dao.NganhDAO;
import dao.NganhTohopDAO;
import dao.TohopMonDAO;
import entity.Nganh;
import entity.NganhTohop;
import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class NganhTohopPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboNganh;

    private static final String[] COLUMNS = {
        "ID", "Mã ngành", "Mã tổ hợp", "Môn 1", "Hệ số 1",
        "Môn 2", "Hệ số 2", "Môn 3", "Hệ số 3",
        "Khóa tổ hợp", "Độ lệch", "Hành động"
    };

    private final NganhTohopDAO nganhTohopDAO = new NganhTohopDAO();
    private final NganhDAO nganhDAO = new NganhDAO();
    private final TohopMonDAO tohopMonDAO = new TohopMonDAO();
    private List<NganhTohop> cachedData = new ArrayList<>();

    public NganhTohopPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm");
        RoundButton btnReload = RoundButton.secondary("Làm mới");
        btnImport.addActionListener(e -> showImport());
        btnAdd.addActionListener(e -> showDialog(-1));
        btnReload.addActionListener(e -> {
            reloadData();
            loadNganhFilter();
        });

        JPanel topBar = buildTopBar(
            "Ngành – Tổ hợp môn",
            "Danh sách tổ hợp môn áp dụng cho từng ngành",
            btnImport, btnReload, btnAdd
        );

        txtSearch = UIComponents.searchField("Tìm mã ngành, mã tổ hợp...");
        txtSearch.setPreferredSize(new Dimension(220, 30));
        cboNganh = UIComponents.comboBox("Tất cả ngành");
        RoundButton btnSearch = RoundButton.secondary("Tìm");
        btnSearch.addActionListener(e -> doSearch());

        JPanel searchBar = buildSearchBar(
            new JLabel("  Tìm: "), txtSearch,
            new JLabel("  Ngành: "), cboNganh,
            btnSearch
        );

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        reloadData();
        loadNganhFilter();

        int[] widths = {40, 75, 70, 60, 50, 60, 50, 60, 50, 145, 60, 90};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{4, 6, 8, 10}) table.getColumnModel().getColumn(i).setCellRenderer(cr);

        table.getColumn("Hành động").setCellRenderer(new ActionRenderer());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COLUMNS.length - 1 && row >= 0) handleAction(row, e);
            }
        });

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(topBar, BorderLayout.NORTH);
        topSection.add(searchBar, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(topSection, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void reloadData() {
        try {
            cachedData = nganhTohopDAO.findAll();
            fillTable(cachedData);
            loadNganhFilter();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không thể tải dữ liệu ngành - tổ hợp. Kiểm tra DB.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNganhFilter() {
        cboNganh.removeAllItems();
        cboNganh.addItem("Tất cả ngành");
        try {
            List<Nganh> nganhList = nganhDAO.findAll();
            for (Nganh n : nganhList) {
                cboNganh.addItem(n.getMaNganh() + " - " + n.getTenNganh());
            }
        } catch (Exception ex) {
            // ignore if DB is unavailable
        }
    }

    private void fillTable(List<NganhTohop> data) {
        tableModel.setRowCount(0);
        for (NganhTohop n : data) {
            Object[] r = new Object[COLUMNS.length];
            r[0] = n.getId();
            r[1] = n.getMaNganh();
            r[2] = n.getMaToHop();
            r[3] = n.getMon1();
            r[4] = n.getHeSo1();
            r[5] = n.getMon2();
            r[6] = n.getHeSo2();
            r[7] = n.getMon3();
            r[8] = n.getHeSo3();
            r[9] = n.getToHopKey();
            r[10] = n.getDoLech();
            r[11] = "actions";
            tableModel.addRow(r);
        }
    }

    private void doSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String selected = String.valueOf(cboNganh.getSelectedItem());
        String maNganh = "Tất cả ngành".equals(selected) ? "" : selected.split(" - ")[0].trim();

        List<NganhTohop> filtered = cachedData.stream()
            .filter(n -> keyword.isEmpty()
                || (n.getMaNganh() != null && n.getMaNganh().toLowerCase().contains(keyword))
                || (n.getMaToHop() != null && n.getMaToHop().toLowerCase().contains(keyword)))
            .filter(n -> maNganh.isEmpty() || maNganh.equals(n.getMaNganh()))
            .collect(Collectors.toList());
        fillTable(filtered);
    }

    private void showDialog(int row) {
        boolean isEdit = row >= 0;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa Ngành – Tổ hợp" : "Thêm Ngành – Tổ hợp",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(480, 460);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16, 20, 16, 20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String ma  = isEdit ? tableModel.getValueAt(row, 1).toString() : "";
        String mth = isEdit ? tableModel.getValueAt(row, 2).toString() : "";
        String m1  = isEdit ? tableModel.getValueAt(row, 3).toString() : "";
        String h1  = isEdit ? tableModel.getValueAt(row, 4).toString() : "3";
        String m2  = isEdit ? tableModel.getValueAt(row, 5).toString() : "";
        String h2  = isEdit ? tableModel.getValueAt(row, 6).toString() : "3";
        String m3  = isEdit ? tableModel.getValueAt(row, 7).toString() : "";
        String h3  = isEdit ? tableModel.getValueAt(row, 8).toString() : "1";
        String dl  = isEdit ? tableModel.getValueAt(row, 10).toString() : "0.00";

        JTextField[] tfs = new JTextField[7];
        String[][] fields = {
            {"Môn 1:", m1},
            {"Hệ số 1:", h1},
            {"Môn 2:", m2},
            {"Hệ số 2:", h2},
            {"Môn 3:", m3},
            {"Hệ số 3:", h3},
            {"Độ lệch:", dl},
        };
        for (int i = 0; i < fields.length; i++) {
            gc.gridx = 0; gc.gridy = i + 2; gc.weightx = 0.4;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx = 1; gc.weightx = 0.6;
            tfs[i] = UIComponents.formField(fields[i][1]);
            body.add(tfs[i], gc);
        }

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.4;
        body.add(UIComponents.formLabel("Mã ngành:"), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JComboBox<String> cboNganh = UIComponents.comboBox("Chọn ngành");
        loadNganhOptions(cboNganh, ma);
        body.add(cboNganh, gc);

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.4;
        body.add(UIComponents.formLabel("Mã tổ hợp:"), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JComboBox<String> cboToHop = UIComponents.comboBox("Chọn tổ hợp");
        loadToHopOptions(cboToHop, mth);
        body.add(cboToHop, gc);

        // Lock subject fields and auto-fill from to hop mon
        for (int i : new int[]{0, 2, 4}) {
            tfs[i].setEditable(false);
            tfs[i].setBackground(AppTheme.BG_SECONDARY);
        }
        cboToHop.addActionListener(e -> {
            String selected = String.valueOf(cboToHop.getSelectedItem());
            String maToHop = extractMaToHop(selected);
            fillSubjectsFromToHop(tfs, maToHop);
        });
        fillSubjectsFromToHop(tfs, extractMaToHop(String.valueOf(cboToHop.getSelectedItem())));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            try {
                NganhTohop entity = isEdit
                    ? nganhTohopDAO.findById(Integer.valueOf(tableModel.getValueAt(row, 0).toString()))
                    : new NganhTohop();
                if (entity == null) entity = new NganhTohop();

                String maNganh = extractMaNganh(String.valueOf(cboNganh.getSelectedItem()));
                String maToHop = extractMaToHop(String.valueOf(cboToHop.getSelectedItem()));

                if (maNganh.isEmpty() || maToHop.isEmpty()) {
                    JOptionPane.showMessageDialog(d,
                        "Mã ngành và mã tổ hợp không được để trống.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tohopMonDAO.findByMaToHop(maToHop) == null) {
                    JOptionPane.showMessageDialog(d,
                        "Mã tổ hợp chưa có trong Tổ hợp môn. Vui lòng tạo trước.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tfs[0].getText().trim().isEmpty()
                    || tfs[2].getText().trim().isEmpty()
                    || tfs[4].getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(d,
                        "Môn 1/2/3 không được để trống.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                entity.setMaNganh(maNganh);
                entity.setMaToHop(maToHop);
                entity.setMon1(tfs[0].getText().trim());
                entity.setHeSo1(parseByte(tfs[1].getText(), (byte) 3));
                entity.setMon2(tfs[2].getText().trim());
                entity.setHeSo2(parseByte(tfs[3].getText(), (byte) 3));
                entity.setMon3(tfs[4].getText().trim());
                entity.setHeSo3(parseByte(tfs[5].getText(), (byte) 1));
                entity.setDoLech(parseDecimal(tfs[6].getText(), BigDecimal.ZERO));
                entity.setToHopKey(maNganh + "_" + maToHop);
                applySubjectFlags(entity);

                nganhTohopDAO.saveOrUpdate(entity);
                reloadData();
                JOptionPane.showMessageDialog(d,
                    "Đã lưu tổ hợp môn cho ngành.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d,
                    "Không thể lưu dữ liệu. Kiểm tra DB.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        footer.add(cancel); footer.add(save);

        d.setLayout(new BorderLayout());
        d.add(body, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showImport() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import — Ngành và tổ hợp môn");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx)", "xlsx","xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            JOptionPane.showMessageDialog(this, "(Demo) Sẽ import danh sách ngành - tổ hợp.", "Import", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAction(int row, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa");
        JMenuItem del  = new JMenuItem("🗑 Xóa");
        edit.addActionListener(ev -> showDialog(row));
        del.addActionListener(ev -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Xóa ngành - tổ hợp: " + tableModel.getValueAt(row, 1) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                try {
                    Integer id = Integer.valueOf(tableModel.getValueAt(row, 0).toString());
                    nganhTohopDAO.deleteById(id);
                    reloadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa dữ liệu. Kiểm tra ràng buộc DB.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menu.add(edit);
        menu.addSeparator();
        menu.add(del);
        menu.show(table, e.getX(), e.getY());
    }

    private void applySubjectFlags(NganhTohop entity) {
        String[] subjects = {entity.getMon1(), entity.getMon2(), entity.getMon3()};
        entity.setN1(hasSubject(subjects, "N1"));
        entity.setTo(hasSubject(subjects, "TO"));
        entity.setLi(hasSubject(subjects, "LI"));
        entity.setHo(hasSubject(subjects, "HO"));
        entity.setSi(hasSubject(subjects, "SI"));
        entity.setVa(hasSubject(subjects, "VA"));
        entity.setSu(hasSubject(subjects, "SU"));
        entity.setDi(hasSubject(subjects, "DI"));
        entity.setTi(hasSubject(subjects, "TI"));
        entity.setKtpl(hasSubject(subjects, "KTPL"));

        boolean known = hasSubject(subjects, "N1") || hasSubject(subjects, "TO")
            || hasSubject(subjects, "LI") || hasSubject(subjects, "HO")
            || hasSubject(subjects, "SI") || hasSubject(subjects, "VA")
            || hasSubject(subjects, "SU") || hasSubject(subjects, "DI")
            || hasSubject(subjects, "TI") || hasSubject(subjects, "KTPL");
        entity.setKhac(!known);
    }

    private boolean hasSubject(String[] subjects, String code) {
        for (String s : subjects) {
            if (s != null && s.equalsIgnoreCase(code)) return true;
        }
        return false;
    }

    private Byte parseByte(String val, byte fallback) {
        String v = val == null ? "" : val.trim();
        if (v.isEmpty()) return fallback;
        return Byte.parseByte(v);
    }

    private BigDecimal parseDecimal(String val, BigDecimal fallback) {
        String v = val == null ? "" : val.trim();
        if (v.isEmpty()) return fallback;
        return new BigDecimal(v);
    }

    private void fillSubjectsFromToHop(JTextField[] tfs, String maToHop) {
        if (maToHop == null || maToHop.trim().isEmpty()) return;
        entity.TohopMon tohop = tohopMonDAO.findByMaToHop(maToHop);
        if (tohop != null) {
            tfs[0].setText(tohop.getMon1());
            tfs[2].setText(tohop.getMon2());
            tfs[4].setText(tohop.getMon3());
        }
    }

    private void loadNganhOptions(JComboBox<String> cbo, String selectedMaNganh) {
        cbo.removeAllItems();
        cbo.addItem("Chọn ngành");
        try {
            List<Nganh> list = nganhDAO.findAll();
            for (Nganh n : list) {
                String label = n.getMaNganh() + " - " + n.getTenNganh();
                cbo.addItem(label);
                if (selectedMaNganh != null
                    && selectedMaNganh.equalsIgnoreCase(n.getMaNganh())) {
                    cbo.setSelectedItem(label);
                }
            }
        } catch (Exception ex) {
            // ignore if DB is unavailable
        }
    }

    private void loadToHopOptions(JComboBox<String> cbo, String selectedMaToHop) {
        cbo.removeAllItems();
        cbo.addItem("Chọn tổ hợp");
        try {
            List<entity.TohopMon> list = tohopMonDAO.findAll();
            for (entity.TohopMon t : list) {
                String label = t.getMaToHop() + " - " + t.getTenToHop();
                cbo.addItem(label);
                if (selectedMaToHop != null
                    && selectedMaToHop.equalsIgnoreCase(t.getMaToHop())) {
                    cbo.setSelectedItem(label);
                }
            }
        } catch (Exception ex) {
            // ignore if DB is unavailable
        }
    }

    private String extractMaToHop(String selected) {
        if (selected == null) return "";
        String s = selected.trim();
        if (s.equalsIgnoreCase("Chọn tổ hợp")) return "";
        int idx = s.indexOf(" - ");
        return idx > 0 ? s.substring(0, idx).trim() : s;
    }

    private String extractMaNganh(String selected) {
        if (selected == null) return "";
        String s = selected.trim();
        if (s.equalsIgnoreCase("Chọn ngành")) return "";
        int idx = s.indexOf(" - ");
        return idx > 0 ? s.substring(0, idx).trim() : s;
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
