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

    private static final Object[][] DATA = {
        {1,  "7140114", "B03", "TO", 3, "VA", 3, "SI", 1, "7140114_B03", 0.00},
        {2,  "7140114", "C01", "TO", 3, "VA", 3, "LI", 1, "7140114_C01", 1.62},
        {3,  "7140114", "C02", "TO", 3, "VA", 3, "HO", 1, "7140114_C02", 0.00},
        {4,  "7140114", "C03", "TO", 3, "VA", 3, "SU", 1, "7140114_C03", 0.00},
        {5,  "7140114", "C04", "TO", 3, "VA", 3, "DI", 1, "7140114_C04", 0.00},
        {6,  "7140114", "D01", "TO", 3, "VA", 3, "N1", 1, "7140114_D01", 0.00},
        {7,  "7140114", "X01", "TO", 3, "VA", 3, "KTPL",1,"7140114_X01", 0.00},
        {8,  "7140114", "X02", "TO", 3, "VA", 3, "TI", 1, "7140114_X02", 0.00},
        {9,  "7140114", "X03", "TO", 3, "VA", 3, "CNCN",1,"7140114_X03", 0.00},
        {10, "7140114", "X04", "TO", 3, "VA", 3, "CNNN",1,"7140114_X04", 0.00},
        {11, "7140201", "M01", "NK1",1, "NK2",1, "VA", 1, "7140201_M01", 0.00},
        {12, "7140201", "M02", "TO", 1, "NK1", 1,"NK2",1, "7140201_M02", 0.00},
        {13, "7140202", "B03", "TO", 3, "VA", 3, "SI", 1, "7140202_B03", 0.00},
        {14, "7140202", "C01", "TO", 3, "VA", 3, "LI", 1, "7140202_C01", 0.00},
        {15, "7140202", "D01", "TO", 3, "VA", 3, "N1", 1, "7140202_D01", 0.00},
        {16, "7140209", "A00", "TO", 3, "LI", 3, "HO", 3, "7140209_A00", 0.00},
        {17, "7140209", "A01", "TO", 3, "LI", 3, "N1", 3, "7140209_A01", 0.00},
        {18, "7140217", "C00", "VA", 3, "SU", 3, "DI", 3, "7140217_C00", 0.00},
        {19, "7140231", "D01", "TO", 3, "VA", 3, "N1", 3, "7140231_D01", 0.00},
        {20, "7140231", "D14", "TO", 3, "VA", 3, "N1", 3, "7140231_D14", 0.00},
    };

    public NganhTohopPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm");
        btnImport.addActionListener(e -> showImport());
        btnAdd.addActionListener(e -> showDialog(-1));

        JPanel topBar = buildTopBar(
            "Ngành – Tổ hợp môn",
            "Danh sách tổ hợp môn áp dụng cho từng ngành",
            btnImport, btnAdd
        );

        txtSearch = UIComponents.searchField("Tìm mã ngành, mã tổ hợp...");
        txtSearch.setPreferredSize(new Dimension(220, 30));
        cboNganh = UIComponents.comboBox("Tất cả ngành",
            "7140114 - QL Giáo dục", "7140201 - GD Mầm non",
            "7140202 - GD Tiểu học", "7140209 - SP Toán học",
            "7140217 - SP Ngữ văn",  "7140231 - SP Tiếng Anh");
        RoundButton btnSearch = RoundButton.secondary("Tìm");
        btnSearch.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "(Demo) Lọc theo ngành đã chọn.", "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE));

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
        loadData();

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
                if (col == COLUMNS.length - 1 && row >= 0) showDialog(row);
            }
        });

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(topBar, BorderLayout.NORTH);
        topSection.add(searchBar, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(topSection, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Object[] row : DATA) {
            Object[] r = new Object[COLUMNS.length];
            System.arraycopy(row, 0, r, 0, row.length);
            r[COLUMNS.length - 1] = "actions";
            tableModel.addRow(r);
        }
    }

    private void showDialog(int row) {
        boolean isEdit = row >= 0;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa Ngành – Tổ hợp" : "Thêm Ngành – Tổ hợp",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(460, 380);
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

        String[][] fields = {
            {"Mã ngành:", ma},
            {"Mã tổ hợp:", mth},
            {"Môn 1:", m1},
            {"Hệ số 1:", h1},
            {"Môn 2:", m2},
            {"Hệ số 2:", h2},
            {"Môn 3:", m3},
            {"Hệ số 3:", h3},
            {"Độ lệch:", dl},
        };
        JTextField[] tfs = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.4;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx = 1; gc.weightx = 0.6;
            tfs[i] = UIComponents.formField(fields[i][1]);
            body.add(tfs[i], gc);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(d,
                "(Demo) Đã lưu tổ hợp môn cho ngành.",
                "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
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
