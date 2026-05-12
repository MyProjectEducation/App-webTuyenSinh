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

public class DiemCongPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;

    private static final String[] COLUMNS = {
        "ID", "CCCD", "Ngành", "Nguyện vọng",
        "Tổ hợp", "Điểm cộng Tiếng Anh", "Điểm cộng HSG", "Tổng điểm cộng",
        "Ghi chú", "Hành động"
    };

    private static final Object[][] DATA = {
        {1,  "056307010216", "7340101", 1, "B00", 0.25, 0.75, 1.00, "Ưu tiên khu vực"},
        {2,  "056307010216", "7340101", 2, "X22", 0.25, 0.75, 1.00, "Ưu tiên khu vực"},
        {3,  "056307010216", "7340101", 3, "B02", 0.25, 0.75, 1.00, "Ưu tiên khu vực"},
        {4,  "056307010216", "7340101", 4, "B01", 0.25, 0.75, 1.00, "Ưu tiên khu vực"},
        {5,  "056307010216", "7340101", 5, "A07", 0.25, 0.75, 1.00, "Ưu tiên khu vực"},
        {6,  "001207008830", "7140114", 2, "D01", 0.50, 0.75, 1.25, "KV1 + UT3"},
        {7,  "001207012439", "7140202", 4, "C01", 2.00, 0.75, 2.75, "KV1 + UT1"},
        {8,  "001207009704", "7140201", 1, "M01", 0.00, 0.50, 0.50, "KV2-NT"},
    };

    public DiemCongPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm điểm cộng");
        btnImport.addActionListener(e -> showImport());
        btnAdd.addActionListener(e -> showDialog(-1));

        JPanel topBar = buildTopBar(
            "Điểm cộng",
            "Điểm cộng theo thí sinh, nguyện vọng và tổ hợp môn",
            btnImport, btnAdd
        );

        txtSearch = UIComponents.searchField("Tìm theo CCCD thí sinh...");
        txtSearch.setPreferredSize(new Dimension(250, 30));
        RoundButton btnSearch = RoundButton.secondary("Tìm");
        btnSearch.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "→ diemCongDAO.search(cccd=\"" + txtSearch.getText() + "\")", "Search", JOptionPane.INFORMATION_MESSAGE));

        JPanel searchBar = buildSearchBar(new JLabel("  Tìm: "), txtSearch, btnSearch);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData();

        int[] widths = {45, 110, 80, 85, 70, 120, 120, 110, 160, 90};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Decimal renderer
        DefaultTableCellRenderer numRender = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                if (v == null) { setText("—"); setForeground(AppTheme.TEXT_THIRD); }
                else { setText(String.format("%.2f", Double.parseDouble(v.toString()))); setForeground(AppTheme.TEXT_PRIMARY); }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        };
        for (int i : new int[]{5, 6}) table.getColumnModel().getColumn(i).setCellRenderer(numRender);

        // diemTong highlighted
        DefaultTableCellRenderer tongRender = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null) {
                    double d = Double.parseDouble(v.toString());
                    setText(String.format("%.2f", d));
                    setForeground(d > 0 ? AppTheme.GREEN : AppTheme.TEXT_SECOND);
                    setFont(AppTheme.FONT_BOLD);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        };
        table.getColumnModel().getColumn(7).setCellRenderer(tongRender);

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

    private void loadData() {
        tableModel.setRowCount(0);
        for (Object[] row : DATA) {
            Object[] r = new Object[COLUMNS.length];
            System.arraycopy(row, 0, r, 0, Math.min(row.length, COLUMNS.length - 1));
            r[COLUMNS.length - 1] = "actions";
            tableModel.addRow(r);
        }
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
            isEdit ? "Sửa điểm cộng" : "Thêm điểm cộng",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(440, 360);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16, 20, 16, 20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String[][] fields = {
            {"CCCD:",                 isEdit ? safe(tableModel.getValueAt(row,1)) : ""},
            {"Mã ngành:",              isEdit ? safe(tableModel.getValueAt(row,2)) : ""},
            {"Nguyện vọng:",           isEdit ? safe(tableModel.getValueAt(row,3)) : "1"},
            {"Tổ hợp môn:",            isEdit ? safe(tableModel.getValueAt(row,4)) : ""},
            {"Điểm cộng Tiếng Anh:",   isEdit ? safe(tableModel.getValueAt(row,5)) : "0.00"},
            {"Điểm cộng HSG:",         isEdit ? safe(tableModel.getValueAt(row,6)) : "0.00"},
            {"Tổng điểm cộng:",        isEdit ? safe(tableModel.getValueAt(row,7)) : "0.00"},
            {"Ghi chú:",               isEdit ? safe(tableModel.getValueAt(row,8)) : ""},
        };
        JTextField[] tfs = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0.4;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx=1; gc.weightx=0.6;
            tfs[i] = UIComponents.formField(fields[i][1]);
            body.add(tfs[i], gc);
        }

        JLabel hint = new JLabel("  Tổng điểm cộng = Điểm cộng Tiếng Anh + Điểm cộng HSG");
        hint.setFont(AppTheme.FONT_SMALL);
        hint.setForeground(AppTheme.TEXT_SECOND);
        gc.gridx=0; gc.gridy=fields.length; gc.gridwidth=2;
        body.add(hint, gc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "(Demo) Đã lưu điểm cộng.", "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
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
        fc.setDialogTitle("Import — Điểm cộng");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx)", "xlsx","xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            JOptionPane.showMessageDialog(this, "(Demo) Sẽ import danh sách điểm cộng.", "Import", JOptionPane.INFORMATION_MESSAGE);
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
