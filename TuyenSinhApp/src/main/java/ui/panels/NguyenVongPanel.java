package ui.panels;

import dao.NguyenVongDAO;
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
import java.util.List;

public class NguyenVongPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboKetqua;

    // Tích hợp dữ liệu thật
    private NguyenVongDAO nvDAO = new NguyenVongDAO();
    private List<Object[]> dbData = new ArrayList<>();

    private static final String[] COLUMNS = {
        "CCCD", "Họ tên", "Nguyện vọng", "Ngành",
        "Tổ hợp cao nhất", "Điểm tổ hợp", "Điểm cộng",
        "Điểm ưu tiên", "Điểm xét tuyển", "Phương thức",
        "Kết quả", "Hành động"
    };

    public NguyenVongPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
        refreshData(); // Nạp dữ liệu thật ngay khi mở panel
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
        btnS.addActionListener(e -> JOptionPane.showMessageDialog(this, "(Chức năng tìm kiếm cục bộ đang hoạt động)", "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE));
        JPanel searchBar = buildSearchBar(new JLabel("  Tìm: "), txtSearch, btnS);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);

        int[] w = {110, 140, 80, 85, 110, 85, 85, 85, 95, 90, 90, 80};
        for (int i = 0; i < w.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // Định dạng hiển thị điểm số an toàn (tránh NullPointerException)
        DefaultTableCellRenderer scoreR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null && !v.toString().trim().isEmpty()) {
                    try {
                        setText(String.format("%.5f", Double.parseDouble(v.toString())));
                    } catch (Exception ex) { setText(v.toString()); }
                } else {
                    setText("0.00000");
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        };
        for (int i : new int[]{5,6,7}) table.getColumnModel().getColumn(i).setCellRenderer(scoreR);

        // Cột điểm xét tuyển nổi bật thu hút ánh nhìn
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null && !v.toString().trim().isEmpty()) {
                    try {
                        setText(String.format("%.5f", Double.parseDouble(v.toString())));
                    } catch (Exception ex) { setText(v.toString()); }
                    setForeground(AppTheme.PRIMARY); 
                    setFont(AppTheme.FONT_BOLD);
                } else {
                    setText("0.00000");
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        // Vẽ trạng thái Badge trúng tuyển/dưới sàn
        table.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = (v != null) ? v.toString().trim() : "";
                if ("Trúng tuyển".equals(val)) { setForeground(AppTheme.GREEN); setText("✓ Trúng tuyển"); }
                else if ("Dưới sàn".equals(val)) { setForeground(AppTheme.AMBER); setText("↓ Dưới sàn"); }
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

    /**
     * Hàm đồng bộ làm mới dữ liệu từ Database
     */
    public void refreshData() {
        List<Object[]> list = nvDAO.getAllForPanel();
        dbData.clear();
        if (list != null) {
            dbData.addAll(list);
        }
        filterData(); 
    }

    private void loadData(List<Object[]> data) {
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
        if (sel.startsWith("Tất cả")) { loadData(dbData); return; }
        
        List<Object[]> filtered = new ArrayList<>();
        for (Object[] row : dbData) {
            String status = (row[10] != null) ? row[10].toString().trim() : "";
            if (status.isEmpty()) status = "Chưa xét";
            
            if (sel.equals(status)) {
                filtered.add(row);
            }
        }
        loadData(filtered);
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

        JLabel formula = new JLabel("  Điểm xét tuyển = Điểm tổ hợp + Điểm ưu tiên + Điểm cộng");
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
            JOptionPane.showMessageDialog(d, "Đã thực hiện cập nhật.", "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Sẽ import danh sách nguyện vọng.", "Import", JOptionPane.INFORMATION_MESSAGE);
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