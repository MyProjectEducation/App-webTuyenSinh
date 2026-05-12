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

public class NguyenVongPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboKetqua;

    private static final String[] COLUMNS = {
        "CCCD", "Họ tên", "Nguyện vọng", "Ngành",
        "Tổ hợp cao nhất", "Điểm tổ hợp", "Điểm cộng",
        "Điểm ưu tiên", "Điểm xét tuyển", "Phương thức",
        "Kết quả", "Hành động"
    };

    private static final Object[][] DATA = {
        {"001207004846", "Nguyễn Thị An",   5,  "7810002", "D01", 16.52, 0.00, 0.25, 16.77, "PT2", "Dưới sàn"},
        {"001207005157", "Trần Thị Bình",   25, "7510301", "C01", 15.70, 0.00, 0.00, 15.70, "PT2", "Dưới sàn"},
        {"001207005157", "Trần Thị Bình",   26, "7510302", "C01", 15.70, 0.00, 0.00, 15.70, "PT2", "Dưới sàn"},
        {"001207006913", "Lê Văn Cường",    27, "7220201", "A00", 15.70, 0.00, 0.00, 15.70, "PT2", "Dưới sàn"},
        {"001207006913", "Lê Văn Cường",    4,  "7220201", "A00", 20.25, 0.00, 0.00, 20.25, "PT2", "Chưa xét"},
        {"001207008830", "Hoàng Văn Em",    2,  "7310401", "D01", 21.18, 0.00, 0.00, 21.18, "PT2", "Trúng tuyển"},
        {"001207009704", "Vũ Thị Phương",   8,  "7340101", "M01", 20.54, 0.00, 1.50, 22.04, "PT2", "Trúng tuyển"},
        {"001207012341", "Bùi Văn Hùng",    1,  "7380101", "A01", 19.93, 0.00, 0.00, 19.93, "PT2", "Trúng tuyển"},
        {"001207012439", "Ngô Thị Lan",     9,  "7480201", "C01", 23.83, 0.00, 0.21, 24.04, "PT2", "Trúng tuyển"},
    };

    public NguyenVongPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm NV");
        btnImport.addActionListener(e -> showImport());
        btnAdd.addActionListener(e -> showDialog(-1));

        cboKetqua = UIComponents.comboBox("Tất cả kết quả", "Trúng tuyển", "Dưới sàn", "Chưa xét");
        cboKetqua.addActionListener(e -> filterData());

        JPanel topBar = buildTopBar(
            "Điểm xét tuyển theo nguyện vọng",
            "ĐGNL/V-SAT được quy đổi thang 30 trước khi tính điểm xét tuyển",
            cboKetqua, btnImport, btnAdd
        );

        txtSearch = UIComponents.searchField("Tìm CCCD, họ tên, ngành...");
        txtSearch.setPreferredSize(new Dimension(250, 30));
        RoundButton btnS = RoundButton.secondary("Tìm");
        btnS.addActionListener(e -> JOptionPane.showMessageDialog(this, "(Demo) Tìm theo CCCD, họ tên hoặc ngành.", "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE));
        JPanel searchBar = buildSearchBar(new JLabel("  Tìm: "), txtSearch, btnS);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData(DATA);

        int[] w = {110, 140, 80, 85, 110, 85, 85, 85, 95, 90, 90, 80};
        for (int i = 0; i < w.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // Score renderer
        DefaultTableCellRenderer scoreR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null) setText(String.format("%.5f", Double.parseDouble(v.toString())));
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        };
        for (int i : new int[]{5,6,7}) table.getColumnModel().getColumn(i).setCellRenderer(scoreR);

        // diem_xettuyen highlighted
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null) { setText(String.format("%.5f", Double.parseDouble(v.toString()))); setForeground(AppTheme.PRIMARY); setFont(AppTheme.FONT_BOLD); }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        // ket qua badge
        table.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("Trúng tuyển".equals(v)) { setForeground(AppTheme.GREEN); setText("✓ Trúng tuyển"); }
                else if ("Dưới sàn".equals(v)) { setForeground(AppTheme.AMBER); setText("↓ Dưới sàn"); }
                else { setForeground(AppTheme.TEXT_THIRD); setText("— Chưa xét"); }
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
            System.arraycopy(row, 0, r, 0, Math.min(row.length, COLUMNS.length - 1));
            r[COLUMNS.length - 1] = "actions";
            tableModel.addRow(r);
        }
    }

    private void filterData() {
        String sel = cboKetqua.getSelectedItem().toString();
        if (sel.startsWith("Tất cả")) { loadData(DATA); return; }
        String kq = sel.equals("Chưa xét") ? "Chưa xét" : sel;
        java.util.List<Object[]> filtered = new java.util.ArrayList<>();
        for (Object[] row : DATA)
            if (kq.equals(row[10]))
                filtered.add(row);
        loadData(filtered.toArray(new Object[0][]));
    }

    private void handleAction(int row, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa NV");
        JMenuItem del  = new JMenuItem("🗑 Xóa");
        edit.addActionListener(ev -> showDialog(row));
        del.addActionListener(ev -> tableModel.removeRow(row));
        menu.add(edit); menu.addSeparator(); menu.add(del);
        menu.show(table, e.getX(), e.getY());
    }

    private void showDialog(int row) {
        boolean isEdit = row >= 0;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa nguyện vọng" : "Thêm nguyện vọng",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(440, 420);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16,20,16,20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String[][] fields = {
            {"CCCD:",                isEdit ? safe(tableModel.getValueAt(row,0)) : ""},
            {"Họ tên:",              isEdit ? safe(tableModel.getValueAt(row,1)) : ""},
            {"Nguyện vọng:",         isEdit ? safe(tableModel.getValueAt(row,2)) : "1"},
            {"Mã ngành:",            isEdit ? safe(tableModel.getValueAt(row,3)) : ""},
            {"Tổ hợp cao nhất:",     isEdit ? safe(tableModel.getValueAt(row,4)) : ""},
            {"Điểm tổ hợp:",         isEdit ? safe(tableModel.getValueAt(row,5)) : "0.00000"},
            {"Điểm cộng:",           isEdit ? safe(tableModel.getValueAt(row,6)) : "0.00"},
            {"Điểm ưu tiên:",        isEdit ? safe(tableModel.getValueAt(row,7)) : "0.00"},
            {"Điểm xét tuyển:",      isEdit ? safe(tableModel.getValueAt(row,8)) : "0.00000"},
            {"Phương thức:",         isEdit ? safe(tableModel.getValueAt(row,9)) : "PT2"},
            {"Kết quả:",             isEdit ? safe(tableModel.getValueAt(row,10)) : "Chưa xét"},
        };
        JTextField[] tfs = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0.4;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx=1; gc.weightx=0.6;
            tfs[i] = UIComponents.formField(fields[i][1]);
            body.add(tfs[i], gc);
        }

        JLabel formula = new JLabel("  Điểm xét tuyển = Điểm tổ hợp + Điểm ưu tiên + Điểm cộng (ĐGNL/V-SAT quy đổi thang 30)");
        formula.setFont(AppTheme.FONT_SMALL);
        formula.setForeground(AppTheme.PRIMARY);
        gc.gridx=0; gc.gridy=fields.length; gc.gridwidth=2;
        body.add(formula, gc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu NV");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "(Demo) Đã lưu thông tin điểm xét tuyển.", "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
        });
        footer.add(cancel); footer.add(save);
        d.setLayout(new BorderLayout());
        d.add(new JScrollPane(body), BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private String safe(Object o) { return o == null ? "" : o.toString(); }

    private void showImport() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import — Nguyện vọng");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel", "xlsx","xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            JOptionPane.showMessageDialog(this, "(Demo) Sẽ import danh sách nguyện vọng.", "Import", JOptionPane.INFORMATION_MESSAGE);
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
