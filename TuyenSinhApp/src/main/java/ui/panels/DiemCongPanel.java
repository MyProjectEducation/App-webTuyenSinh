package ui.panels;

import dao.DiemCongDAO;
import entity.DiemCong;
import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;

public class DiemCongPanel extends BasePanel {

    private final DiemCongDAO diemCongDAO = new DiemCongDAO();

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;

    /** Thứ tự khớp bảng `xt_diemcongxetuyen` trong dataNow.sql + cột thao tác UI */
    private static final String[] COLUMNS = {
        "ID", "CCCD", "Điểm tổng", "Mã ngành",
        "Mã tổ hợp", "Phương thức", "Ghi chú",
        "dc_keys", "Điểm CC", "Điểm ƯTXT", "Hành động"
    };

    private static final int COL_ID = 0;
    private static final int COL_CCCD = 1;
    private static final int COL_DIEM_TONG = 2;
    private static final int COL_MA_NGANH = 3;
    private static final int COL_MA_TOHOP = 4;
    private static final int COL_PHUONG_THUC = 5;
    private static final int COL_GHI_CHU = 6;
    private static final int COL_DC_KEYS = 7;
    private static final int COL_DIEM_CC = 8;
    private static final int COL_DIEM_UTXT = 9;
    private static final int COL_ACTIONS = 10;

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
        btnSearch.addActionListener(e -> loadData());

        JPanel searchBar = buildSearchBar(new JLabel("  Tìm: "), txtSearch, btnSearch);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData();

        int[] widths = {52, 120, 88, 88, 72, 88, 200, 160, 80, 88, 100};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer numRender = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v == null) {
                    setText("—");
                    if (!sel) setForeground(AppTheme.TEXT_THIRD);
                } else {
                    setText(String.format("%.2f", new BigDecimal(v.toString()).doubleValue()));
                    if (!sel) setForeground(AppTheme.TEXT_PRIMARY);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        };
        for (int i : new int[]{COL_DIEM_TONG, COL_DIEM_CC, COL_DIEM_UTXT})
            table.getColumnModel().getColumn(i).setCellRenderer(numRender);

        DefaultTableCellRenderer tongRender = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null) {
                    double d = new BigDecimal(v.toString()).doubleValue();
                    setText(String.format("%.2f", d));
                    setForeground(d > 0 ? AppTheme.GREEN : AppTheme.TEXT_SECOND);
                    setFont(AppTheme.FONT_BOLD);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        };
        table.getColumnModel().getColumn(COL_DIEM_TONG).setCellRenderer(tongRender);

        table.getColumn("Hành động").setCellRenderer(new ActionRenderer());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COL_ACTIONS && row >= 0) handleAction(row, e);
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
        try {
            List<DiemCong> list = diemCongDAO.findByCccdContaining(txtSearch.getText());
            for (DiemCong d : list) {
                tableModel.addRow(new Object[]{
                    d.getId(),
                    d.getCccd(),
                    d.getDiemTong(),
                    d.getMaNganh(),
                    d.getMaToHop(),
                    d.getPhuongThuc(),
                    d.getGhiChu(),
                    d.getDcKeys(),
                    d.getDiemCC(),
                    d.getDiemUtxt(),
                    "actions"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không tải được dữ liệu từ database:\n" + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAction(int row, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa");
        JMenuItem del  = new JMenuItem("🗑 Xóa");
        edit.addActionListener(ev -> showDialog(row));
        del.addActionListener(ev -> {
            try {
                Integer id = (Integer) tableModel.getValueAt(row, COL_ID);
                diemCongDAO.deleteById(id);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không xóa được: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(edit); menu.addSeparator(); menu.add(del);
        menu.show(table, e.getX(), e.getY());
    }

    private void showDialog(int row) {
        boolean isEdit = row >= 0;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa điểm cộng" : "Thêm điểm cộng",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(480, 420);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16, 20, 16, 20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String[][] fields = {
            {"CCCD:",              isEdit ? safe(tableModel.getValueAt(row, COL_CCCD)) : ""},
            {"Mã ngành:",          isEdit ? safe(tableModel.getValueAt(row, COL_MA_NGANH)) : ""},
            {"Mã tổ hợp:",         isEdit ? safe(tableModel.getValueAt(row, COL_MA_TOHOP)) : ""},
            {"Phương thức:",       isEdit ? safe(tableModel.getValueAt(row, COL_PHUONG_THUC)) : ""},
            {"Điểm CC:",           isEdit ? safe(tableModel.getValueAt(row, COL_DIEM_CC)) : "0.00"},
            {"Điểm ƯTXT:",         isEdit ? safe(tableModel.getValueAt(row, COL_DIEM_UTXT)) : "0.00"},
            {"Điểm tổng:",         isEdit ? safe(tableModel.getValueAt(row, COL_DIEM_TONG)) : "0.00"},
            {"dc_keys:",           isEdit ? safe(tableModel.getValueAt(row, COL_DC_KEYS)) : ""},
            {"Ghi chú:",           isEdit ? safe(tableModel.getValueAt(row, COL_GHI_CHU)) : ""},
        };
        JTextField[] tfs = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.35;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx = 1; gc.weightx = 0.65;
            tfs[i] = UIComponents.formField(fields[i][1]);
            body.add(tfs[i], gc);
        }

        JLabel hint = new JLabel("  Dữ liệu nguồn: bảng xt_diemcongxetuyen (xem dataNow.sql). Thêm/Sửa qua form sẽ được nối DAO sau.");
        hint.setFont(AppTheme.FONT_SMALL);
        hint.setForeground(AppTheme.TEXT_SECOND);
        gc.gridx = 0; gc.gridy = fields.length; gc.gridwidth = 2;
        body.add(hint, gc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "(Demo) Lưu entity — cần DiemCongDAO.saveOrUpdate khi triển khai đầy đủ.",
                "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
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
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx)", "xlsx", "xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            JOptionPane.showMessageDialog(this, "(Demo) Sẽ import danh sách điểm cộng.", "Import", JOptionPane.INFORMATION_MESSAGE);
    }

    static class ActionRenderer extends DefaultTableCellRenderer {
        private final JPanel panel;

        ActionRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 2));
            panel.setOpaque(true);
            JButton e = new JButton("Sửa"); e.setFont(AppTheme.FONT_SMALL);
            e.setBackground(AppTheme.BG_SECONDARY); e.setForeground(AppTheme.TEXT_PRIMARY);
            e.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER)); e.setFocusPainted(false);
            JButton del = new JButton("Xóa"); del.setFont(AppTheme.FONT_SMALL);
            del.setBackground(AppTheme.RED_LIGHT); del.setForeground(AppTheme.RED);
            del.setBorder(BorderFactory.createLineBorder(AppTheme.RED_LIGHT)); del.setFocusPainted(false);
            panel.add(e); panel.add(del);
        }

        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            if (sel) {
                panel.setBackground(t.getSelectionBackground());
            } else {
                panel.setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
            }
            return panel;
        }
    }
}
