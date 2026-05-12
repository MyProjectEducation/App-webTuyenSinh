package ui.panels;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class DiemThiPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboPhuongThuc;

    // Columns matching xt_diemthixettuyen schema
    private static final String[] COLUMNS = {
        "ID", "CCCD", "Số báo danh", "Phương thức",
        "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Ngữ văn",
        "Anh (thi)", "Anh (chứng chỉ)", "Công nghệ CN", "Công nghệ NN", "Tin học", "KTPL",
        "ĐGNL", "Năng khiếu 1", "Năng khiếu 2", "Hành động"
    };

    private static final Object[][] DATA = {
        {3,  "001207004846","001207004846","PT4", 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, null, 0.00, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {4,  "001207005157","001207005157","PT4", 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, null, 0.00, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {5,  "001207006913","001207006913","PT4", 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 10.00,0.00, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {6,  "001207006593","001207006593","PT4", 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, null, 0.00, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {7,  "001207008830","001207008830","PT4", 5.16, 0.00, 0.00, 0.00, 0.00, 0.00, 7.53, 7.18, 7.18, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {8,  "001207009704","001207009704","PT4", 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, null, 0.00, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {9,  "001207011459","001207011459","PT4", 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, null, 0.00, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {10, "001207012341","001207012341","PT4", 6.07, 0.00, 0.00, 8.77, 0.00, 0.00, 9.64, 9.31, null, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {11, "001207012439","001207012439","PT4", 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, null, 0.00, 0.00, 0.00, 0.00, 0.00, null, null, null},
        {12, "001207012684","001207012684","PT4", 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, null, 0.00, 0.00, 0.00, 0.00, 0.00, null, null, null},
        // VSAT samples
        {50, "001207050001","001207050001","PT2", null, null, null, null, null, null, null, null, null, null, null, null, null, null, 750.0, null},
        {51, "001207050002","001207050002","PT2", null, null, null, null, null, null, null, null, null, null, null, null, null, null, 820.0, null},
        // DGNL samples
        {100,"001207100001","001207100001","PT3", null, null, null, null, null, null, null, null, null, null, null, null, null, 920.0, null, null},
    };

    public DiemThiPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm điểm");
        btnImport.addActionListener(e -> showImport());
        btnAdd.addActionListener(e -> showAddDialog());

        cboPhuongThuc = UIComponents.comboBox(
            "Tất cả phương thức",
            "PT4 — THPT Quốc gia",
            "PT2 — V-SAT 2025",
            "PT3 — ĐGNL"
        );
        cboPhuongThuc.addActionListener(e -> filterByPhuongThuc());

        JPanel topBar = buildTopBar(
            "Điểm thi",
            "Điểm THPT, ĐGNL, V-SAT (quy đổi thang 30 khi xét tuyển)",
            cboPhuongThuc, btnImport, btnAdd
        );

        JPanel statRow = new JPanel(new GridLayout(1, 4, 10, 0));
        statRow.setOpaque(false);
        statRow.setBorder(new EmptyBorder(10, 14, 8, 14));
        statRow.add(UIComponents.statCard("THPT", "12", AppTheme.PRIMARY, "Hồ sơ có điểm"));
        statRow.add(UIComponents.statCard("V-SAT", "2", AppTheme.GREEN, "Hồ sơ có điểm"));
        statRow.add(UIComponents.statCard("ĐGNL", "1", AppTheme.AMBER, "Hồ sơ có điểm"));
        statRow.add(UIComponents.statCard("Môn có điểm", "7", AppTheme.RED, "Theo dữ liệu mẫu"));

        txtSearch = UIComponents.searchField("Tìm CCCD, số báo danh...");
        txtSearch.setPreferredSize(new Dimension(240, 30));
        RoundButton btnSearch = RoundButton.secondary("Tìm");
        btnSearch.addActionListener(e -> doSearch());

        JPanel searchBar = buildSearchBar(
            new JLabel("  Tìm: "), txtSearch, btnSearch
        );

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData(DATA);

        int[] widths = {55, 110, 110, 75, 45, 45, 45, 45, 45, 45, 45, 55, 55, 50, 50, 45, 50, 45, 50, 50, 80};
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
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COLUMNS.length - 1 && row >= 0) handleAction(row, e);
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

    private void loadData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            Object[] r = new Object[COLUMNS.length];
            System.arraycopy(row, 0, r, 0, Math.min(row.length, COLUMNS.length - 1));
            r[COLUMNS.length - 1] = "actions";
            tableModel.addRow(r);
        }
    }

    private void filterByPhuongThuc() {
        String sel = cboPhuongThuc.getSelectedItem().toString();
        if (sel.startsWith("Tất cả")) { loadData(DATA); return; }
        String pt = sel.startsWith("PT4") ? "PT4" : sel.startsWith("PT2") ? "PT2" : "PT3";
        java.util.List<Object[]> filtered = new java.util.ArrayList<>();
        for (Object[] row : DATA)
            if (row[3].equals(pt)) filtered.add(row);
        loadData(filtered.toArray(new Object[0][]));
    }

    private void doSearch() {
        JOptionPane.showMessageDialog(this,
            "(Demo) Tìm theo CCCD/số báo danh và phương thức đã chọn.",
            "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAction(int row, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa điểm");
        JMenuItem del  = new JMenuItem("🗑 Xóa");
        edit.addActionListener(ev -> showEditDialog(row));
        del.addActionListener(ev -> tableModel.removeRow(row));
        menu.add(edit); menu.addSeparator(); menu.add(del);
        menu.show(table, e.getX(), e.getY());
    }

    private void showAddDialog() { showDialog(-1); }
    private void showEditDialog(int row) { showDialog(row); }

    private void showDialog(int row) {
        boolean isEdit = row >= 0;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa điểm thi" : "Thêm điểm thi",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(520, 480);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(14, 18, 14, 18));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 5, 4, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String cccd = isEdit ? tableModel.getValueAt(row, 1).toString() : "";
        String sbd  = isEdit ? tableModel.getValueAt(row, 2).toString() : "";
        String pt   = isEdit ? tableModel.getValueAt(row, 3).toString() : "PT4";

        String[][] fields = {
            {"CCCD:", cccd},
            {"Số báo danh:", sbd},
            {"Phương thức:", pt},
            {"Toán:", isEdit ? nullSafe(tableModel.getValueAt(row, 4)) : ""},
            {"Ngữ văn:", isEdit ? nullSafe(tableModel.getValueAt(row, 10)) : ""},
            {"Vật lý:", isEdit ? nullSafe(tableModel.getValueAt(row, 5)) : ""},
            {"Hóa học:", isEdit ? nullSafe(tableModel.getValueAt(row, 6)) : ""},
            {"Sinh học:", isEdit ? nullSafe(tableModel.getValueAt(row, 7)) : ""},
            {"Lịch sử:", isEdit ? nullSafe(tableModel.getValueAt(row, 8)) : ""},
            {"Địa lý:", isEdit ? nullSafe(tableModel.getValueAt(row, 9)) : ""},
            {"Tiếng Anh (thi):", isEdit ? nullSafe(tableModel.getValueAt(row, 11)) : ""},
            {"Tiếng Anh (chứng chỉ):", isEdit ? nullSafe(tableModel.getValueAt(row, 12)) : ""},
            {"Năng khiếu 1:", isEdit ? nullSafe(tableModel.getValueAt(row, 18)) : ""},
            {"Năng khiếu 2:", isEdit ? nullSafe(tableModel.getValueAt(row, 19)) : ""},
        };
        JTextField[] tfs = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            int col = i % 2, rowIdx = i / 2;
            gc.gridx = col * 2; gc.gridy = rowIdx; gc.weightx = 0.3;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx = col * 2 + 1; gc.weightx = 0.5;
            tfs[i] = UIComponents.formField(fields[i][1]);
            body.add(tfs[i], gc);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu điểm");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(d,
                "→ diemThiDAO.save(cccd=" + tfs[0].getText() + ", phuongThuc=" + tfs[2].getText() + ")",
                "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
        });
        footer.add(cancel); footer.add(save);
        d.setLayout(new BorderLayout());
        d.add(new JScrollPane(body), BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private String nullSafe(Object o) { return o == null ? "" : o.toString(); }

    private void showImport() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Import điểm thi", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(380, 220);
        d.setLocationRelativeTo(this);
        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16, 20, 16, 20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 5, 6, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx=0; gc.gridy=0; gc.weightx=0.4;
        body.add(UIComponents.formLabel("Loại điểm:"), gc);
        gc.gridx=1; gc.weightx=0.6;
        JComboBox<String> cbo = UIComponents.comboBox("PT4 — THPT QG", "PT2 — V-SAT", "PT3 — ĐGNL");
        body.add(cbo, gc);

        gc.gridx=0; gc.gridy=1;
        body.add(UIComponents.formLabel("File Excel (.xlsx):"), gc);
        gc.gridx=1;
        JButton btnFile = new JButton("Chọn file...");
        btnFile.setFont(AppTheme.FONT_BODY);
        body.add(btnFile, gc);

        JLabel lblFile = new JLabel("");
        lblFile.setFont(AppTheme.FONT_SMALL);
        lblFile.setForeground(AppTheme.TEXT_SECOND);
        gc.gridx=0; gc.gridy=2; gc.gridwidth=2;
        body.add(lblFile, gc);

        btnFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel", "xlsx","xls"));
            if (fc.showOpenDialog(d) == JFileChooser.APPROVE_OPTION)
                lblFile.setText(fc.getSelectedFile().getName());
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0, AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton imp    = RoundButton.primary("Import");
        cancel.addActionListener(e -> d.dispose());
        imp.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "(Demo) Sẽ import danh sách điểm theo loại đã chọn.", "Import", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
        });
        footer.add(cancel); footer.add(imp);
        d.setLayout(new BorderLayout());
        d.add(body, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // Score cell renderer: null → "NULL" in gray, 0 → "0" in light, value → bold
    static class ScoreCellRenderer extends DefaultTableCellRenderer {
        public ScoreCellRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if (v == null) {
                setText("NULL");
                setForeground(AppTheme.TEXT_THIRD);
                setFont(AppTheme.FONT_SMALL);
            } else {
                double d = Double.parseDouble(v.toString());
                setText(String.format("%.2f", d));
                if (d == 0) { setForeground(AppTheme.TEXT_THIRD); setFont(AppTheme.FONT_SMALL); }
                else { setForeground(AppTheme.TEXT_PRIMARY); setFont(AppTheme.FONT_BOLD); }
            }
            if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
            return c;
        }
    }

    static class PhuongThucRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if ("PT4".equals(v)) { setForeground(AppTheme.PRIMARY); setBackground(AppTheme.PRIMARY_LIGHT); }
            else if ("PT2".equals(v)) { setForeground(AppTheme.GREEN); setBackground(AppTheme.GREEN_LIGHT); }
            else if ("PT3".equals(v)) { setForeground(AppTheme.AMBER); setBackground(AppTheme.AMBER_LIGHT); }
            else { setForeground(AppTheme.TEXT_SECOND); setBackground(AppTheme.BG_SECONDARY); }
            if (sel) setBackground(AppTheme.PRIMARY_LIGHT);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(AppTheme.FONT_SMALL);
            return this;
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
