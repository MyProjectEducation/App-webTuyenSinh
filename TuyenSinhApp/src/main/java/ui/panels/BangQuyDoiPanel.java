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

public class BangQuyDoiPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboPhuongThuc;

    private static final String[] COLUMNS = {
        "ID", "Phương thức", "Tổ hợp", "Môn",
        "Điểm A", "Điểm B", "Điểm C", "Điểm D",
        "Mã quy đổi", "Phân vị", "Hành động"
    };

    private static final Object[][] DATA = {
        {2,  "DGNL","A01",null,998.0, 1001.0,25.75,26.10,"DGNL_A01_2", 2},
        {3,  "DGNL","A01",null,984.0, 997.0, 25.05,25.65,"DGNL_A01_3", 3},
        {4,  "DGNL","A01",null,973.0, 983.0, 25.35,25.65,"DGNL_A01_4", 4},
        {5,  "DGNL","A01",null,962.0, 972.0, 25.05,25.25,"DGNL_A01_5", 5},
        {6,  "DGNL","A01",null,954.0, 961.0, 24.85,25.00,"DGNL_A01_6", 6},
        {7,  "DGNL","A01",null,946.0, 953.0, 24.60,24.75,"DGNL_A01_7", 7},
        {8,  "DGNL","A01",null,939.0, 945.0, 24.30,24.50,"DGNL_A01_8", 8},
        {9,  "DGNL","A01",null,932.0, 938.0, 24.25,24.25,"DGNL_A01_9", 9},
        {10, "DGNL","A01",null,926.0, 931.0, 24.00,24.20,"DGNL_A01_10",10},
        {11, "DGNL","A01",null,919.0, 925.0, 23.80,23.95,"DGNL_A01_11",11},
        {12, "DGNL","A01",null,913.0, 918.0, 23.55,23.75,"DGNL_A01_12",12},
        // VSAT
        {100,"VSAT","A00",null,950.0,1000.0,26.00,27.00,"VSAT_A00_1",1},
        {101,"VSAT","A00",null,900.0,949.0, 25.00,25.90,"VSAT_A00_2",2},
        {102,"VSAT","A00",null,850.0,899.0, 24.00,24.90,"VSAT_A00_3",3},
    };

    public BangQuyDoiPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm");
        btnImport.addActionListener(e -> showImport());
        btnAdd.addActionListener(e -> showDialog(-1));

        cboPhuongThuc = UIComponents.comboBox("Tất cả phương thức", "DGNL", "V-SAT");
        cboPhuongThuc.addActionListener(e -> filterData());

        JPanel topBar = buildTopBar(
            "Bảng quy đổi",
            "Quy đổi điểm theo phương thức và tổ hợp",
            cboPhuongThuc, btnImport, btnAdd
        );

        txtSearch = UIComponents.searchField("Tìm theo tổ hợp hoặc mã quy đổi...");
        txtSearch.setPreferredSize(new Dimension(230, 30));
        RoundButton btnS = RoundButton.secondary("Tìm");
        JPanel searchBar = buildSearchBar(new JLabel("  Tìm: "), txtSearch, btnS);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData(DATA);

        int[] w = {45, 80, 65, 60, 70, 70, 70, 70, 120, 60, 80};
        for (int i = 0; i < w.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // Score range renderer
        DefaultTableCellRenderer numR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null) setText(String.format("%.2f", Double.parseDouble(v.toString())));
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        };
        for (int i : new int[]{4,5,6,7}) table.getColumnModel().getColumn(i).setCellRenderer(numR);
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(AppTheme.FONT_MONO);
                setForeground(AppTheme.TEXT_SECOND);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        table.getColumn("Hành động").setCellRenderer(new BtnRenderer());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COLUMNS.length - 1 && row >= 0) handleAction(row, e);
            }
        });

        JPanel topSec = new JPanel(new BorderLayout());
        topSec.add(topBar, BorderLayout.NORTH);
        topSec.add(searchBar, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(topSec, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            Object[] r = new Object[COLUMNS.length];
            System.arraycopy(row, 0, r, 0, Math.min(row.length, COLUMNS.length-1));
            r[COLUMNS.length-1] = "actions";
            tableModel.addRow(r);
        }
    }

    private void filterData() {
        String sel = cboPhuongThuc.getSelectedItem().toString();
        if (sel.startsWith("Tất cả")) { loadData(DATA); return; }
        if ("V-SAT".equals(sel)) sel = "VSAT";
        java.util.List<Object[]> filtered = new java.util.ArrayList<>();
        for (Object[] row : DATA) if (sel.equals(row[1])) filtered.add(row);
        loadData(filtered.toArray(new Object[0][]));
    }

    private void handleAction(int row, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa");
        JMenuItem del  = new JMenuItem("🗑 Xóa");
        edit.addActionListener(ev -> showDialog(row));
        del.addActionListener(ev -> tableModel.removeRow(row));
        menu.add(edit); menu.addSeparator(); menu.add(del);
        menu.show(table, e.getX(), e.getY());
    }

    private void showDialog(int row) {
        boolean isEdit = row >= 0;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa quy đổi" : "Thêm bảng quy đổi",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(430, 360);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16,20,16,20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String[][] fields = {
            {"Phương thức:", isEdit ? safe(tableModel.getValueAt(row,1)) : "DGNL"},
            {"Tổ hợp:",      isEdit ? safe(tableModel.getValueAt(row,2)) : "A01"},
            {"Môn:",         isEdit ? safe(tableModel.getValueAt(row,3)) : ""},
            {"Điểm A:",      isEdit ? safe(tableModel.getValueAt(row,4)) : ""},
            {"Điểm B:",      isEdit ? safe(tableModel.getValueAt(row,5)) : ""},
            {"Điểm C:",      isEdit ? safe(tableModel.getValueAt(row,6)) : ""},
            {"Điểm D:",      isEdit ? safe(tableModel.getValueAt(row,7)) : ""},
            {"Mã quy đổi:",   isEdit ? safe(tableModel.getValueAt(row,8)) : ""},
            {"Phân vị:",      isEdit ? safe(tableModel.getValueAt(row,9)) : ""},
        };
        JTextField[] tfs = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0.4;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx=1; gc.weightx=0.6;
            tfs[i] = UIComponents.formField(fields[i][1]);
            body.add(tfs[i], gc);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "(Demo) Đã lưu bảng quy đổi.", "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
        });
        footer.add(cancel); footer.add(save);
        d.setLayout(new BorderLayout());
        d.add(body, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private String safe(Object o) { return o == null ? "" : o.toString(); }

    private void showImport() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import — Bảng quy đổi");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel", "xlsx","xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            JOptionPane.showMessageDialog(this, "(Demo) Sẽ import bảng quy đổi.", "Import", JOptionPane.INFORMATION_MESSAGE);
    }

    static class BtnRenderer extends DefaultTableCellRenderer {
        private JPanel panel;
        public BtnRenderer() {
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
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            panel.setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
            return panel;
        }
    }
}
