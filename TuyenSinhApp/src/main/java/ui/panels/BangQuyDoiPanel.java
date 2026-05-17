package ui.panels;

import dao.BangQuyDoiDAO;
import entity.BangQuyDoi;
import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BangQuyDoiPanel extends BasePanel {

    private final BangQuyDoiDAO bqdDAO = new BangQuyDoiDAO();

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboPhuongThuc;

    private static final String[] COLUMNS = {
        "ID", "Phương thức", "Tổ hợp", "Môn",
        "Điểm A", "Điểm B", "Điểm C", "Điểm D",
        "Mã quy đổi", "Phân vị", "Hành động"
    };

    private static final int COL_ID = 0;
    private static final int COL_DIEM_A = 4;
    private static final int COL_DIEM_D = 7;
    private static final int COL_ACTIONS = 10;

    public BangQuyDoiPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnRefresh = RoundButton.secondary("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        RoundButton btnAdd = RoundButton.primary("+ Thêm quy đổi");
        btnAdd.addActionListener(e -> showDialog(-1));

        JPanel topBar = buildTopBar(
            "Bảng quy đổi điểm",
            "Dữ liệu từ bảng xt_bangquydoi — quy đổi DGNL / VSAT / ngoại ngữ",
            btnRefresh, btnAdd
        );

        txtSearch = UIComponents.searchField("Mã quy đổi, môn, tổ hợp, phương thức...");
        txtSearch.setPreferredSize(new Dimension(280, 30));
        RoundButton btnSearch = RoundButton.secondary("Tìm");
        btnSearch.addActionListener(e -> loadData());

        cboPhuongThuc = new JComboBox<>(new String[]{
            "Tất cả", "VSAT", "DGNL", "THPT", "IELTS", "TOEIC", "SAT"
        });
        cboPhuongThuc.setPreferredSize(new Dimension(120, 30));
        cboPhuongThuc.addActionListener(e -> loadData());

        JPanel searchBar = buildSearchBar(
            new JLabel("  Tìm: "), txtSearch, btnSearch,
            new JLabel("  Phương thức: "), cboPhuongThuc
        );

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == COL_ACTIONS;
            }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        table.setRowHeight(36);

        int[] widths = {48, 88, 72, 56, 72, 72, 72, 72, 140, 72, 110};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer numRender = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setText(formatScore(v));
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    setForeground(v == null ? AppTheme.TEXT_THIRD : AppTheme.TEXT_PRIMARY);
                    setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                }
                return this;
            }
        };
        for (int i = COL_DIEM_A; i <= COL_DIEM_D; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(numRender);
        }

        table.getColumnModel().getColumn(COL_ACTIONS).setCellRenderer(new ActionPanelRenderer());
        table.getColumnModel().getColumn(COL_ACTIONS).setCellEditor(new ActionPanelEditor());

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(topBar, BorderLayout.NORTH);
        topSection.add(searchBar, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(topSection, BorderLayout.NORTH);
        add(wrapTable(table), BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            String pt = (String) cboPhuongThuc.getSelectedItem();
            List<BangQuyDoi> list = bqdDAO.findFiltered(txtSearch.getText(), pt);
            for (BangQuyDoi b : list) {
                tableModel.addRow(new Object[]{
                    b.getIdqd(),
                    nullToDash(b.getPhuongThuc()),
                    nullToDash(b.getToHop()),
                    nullToDash(b.getMon()),
                    b.getDiemA(),
                    b.getDiemB(),
                    b.getDiemC(),
                    b.getDiemD(),
                    nullToDash(b.getMaQuyDoi()),
                    nullToDash(b.getPhanVi()),
                    ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không tải được dữ liệu từ xt_bangquydoi:\n" + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDialog(int row) {
        boolean isEdit = row >= 0;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa bảng quy đổi" : "Thêm bảng quy đổi",
            Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(480, 520);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16, 20, 16, 20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String[][] fields = {
            {"Phương thức *:",  isEdit ? cellStr(row, 1) : "VSAT"},
            {"Tổ hợp:",         isEdit ? cellStr(row, 2) : ""},
            {"Môn:",            isEdit ? cellStr(row, 3) : ""},
            {"Điểm A *:",       isEdit ? cellStr(row, 4) : ""},
            {"Điểm B:",         isEdit ? cellStr(row, 5) : ""},
            {"Điểm C:",         isEdit ? cellStr(row, 6) : ""},
            {"Điểm D:",         isEdit ? cellStr(row, 7) : ""},
            {"Mã quy đổi *:",   isEdit ? cellStr(row, 8) : ""},
            {"Phân vị:",        isEdit ? cellStr(row, 9) : ""},
        };
        JTextField[] tfs = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            gc.gridx = 0;
            gc.gridy = i;
            gc.weightx = 0.35;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx = 1;
            gc.weightx = 0.65;
            tfs[i] = UIComponents.formField(fields[i][1]);
            body.add(tfs[i], gc);
        }

        final Integer editId = isEdit ? (Integer) tableModel.getValueAt(row, COL_ID) : null;

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save = RoundButton.primary("Lưu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            try {
                BangQuyDoi entity = readEntityFromForm(tfs, editId);
                bqdDAO.save(entity, editId == null);
                loadData();
                d.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Dữ liệu không hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Không lưu được:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        footer.add(cancel);
        footer.add(save);

        d.setLayout(new BorderLayout());
        d.add(body, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void confirmDelete(int id) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xóa bản ghi quy đổi ID " + id + "?",
            "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            bqdDAO.deleteById(id);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không xóa được: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BangQuyDoi readEntityFromForm(JTextField[] tfs, Integer editId) {
        String pt = tfs[0].getText().trim();
        if (pt.isEmpty()) {
            throw new IllegalArgumentException("Phương thức không được để trống.");
        }
        String ma = tfs[7].getText().trim();
        if (ma.isEmpty()) {
            throw new IllegalArgumentException("Mã quy đổi không được để trống.");
        }

        BigDecimal diemA = parseBdRequired(tfs[3].getText(), "Điểm A");

        String toHop = emptyToNull(tfs[1].getText());
        String mon = emptyToNull(tfs[2].getText());
        BigDecimal diemB = parseBdOptional(tfs[4].getText());
        BigDecimal diemC = parseBdOptional(tfs[5].getText());
        BigDecimal diemD = parseBdOptional(tfs[6].getText());
        String phanVi = emptyToNull(tfs[8].getText());

        BangQuyDoi conflict = bqdDAO.findByMaQuyDoi(ma);
        if (conflict != null && (editId == null || conflict.getIdqd() != editId)) {
            throw new IllegalArgumentException("Mã quy đổi \"" + ma + "\" đã tồn tại.");
        }

        BangQuyDoi b = new BangQuyDoi();
        if (editId != null) {
            b.setIdqd(editId);
        }
        b.setPhuongThuc(pt);
        b.setToHop(toHop);
        b.setMon(mon);
        b.setDiemA(diemA);
        b.setDiemB(diemB);
        b.setDiemC(diemC);
        b.setDiemD(diemD);
        b.setMaQuyDoi(ma);
        b.setPhanVi(phanVi);
        return b;
    }

    private String cellStr(int row, int col) {
        Object v = tableModel.getValueAt(row, col);
        if (v == null) return "";
        String s = v.toString().trim();
        return "—".equals(s) ? "" : s;
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static BigDecimal parseBdRequired(String s, String label) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " là bắt buộc.");
        }
        return new BigDecimal(s.trim().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal parseBdOptional(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return new BigDecimal(s.trim().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
    }

    private static String nullToDash(String s) {
        return s == null || s.trim().isEmpty() ? "—" : s.trim();
    }

    private static String formatScore(Object v) {
        if (v == null) return "—";
        if (v instanceof BigDecimal) {
            return ((BigDecimal) v).setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return v.toString();
    }

    // --- Nút Sửa / Xóa trên từng dòng ---

    class ActionPanelRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnSua = actionButton("Sửa", false);
        private final JButton btnXoa = actionButton("Xóa", true);

        ActionPanelRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 2));
            setOpaque(true);
            add(btnSua);
            add(btnXoa);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value,
                boolean selected, boolean focus, int row, int column) {
            setBackground(selected ? tbl.getSelectionBackground()
                : (row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY));
            return this;
        }
    }

    class ActionPanelEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        private final JButton btnSua = actionButton("Sửa", false);
        private final JButton btnXoa = actionButton("Xóa", true);
        private int editingRow = -1;

        ActionPanelEditor() {
            panel.setOpaque(true);
            btnSua.addActionListener(e -> {
                fireEditingStopped();
                if (editingRow >= 0) {
                    showDialog(editingRow);
                }
            });
            btnXoa.addActionListener(e -> {
                fireEditingStopped();
                if (editingRow >= 0) {
                    int id = (Integer) tableModel.getValueAt(editingRow, COL_ID);
                    confirmDelete(id);
                }
            });
            panel.add(btnSua);
            panel.add(btnXoa);
        }

        @Override
        public Component getTableCellEditorComponent(JTable tbl, Object value,
                boolean selected, int row, int column) {
            editingRow = row;
            panel.setBackground(tbl.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private static JButton actionButton(String text, boolean danger) {
        JButton b = new JButton(text);
        b.setFont(AppTheme.FONT_SMALL);
        b.setFocusPainted(false);
        if (danger) {
            b.setBackground(AppTheme.RED_LIGHT);
            b.setForeground(AppTheme.RED);
            b.setBorder(BorderFactory.createLineBorder(AppTheme.RED_LIGHT));
        } else {
            b.setBackground(AppTheme.BG_SECONDARY);
            b.setForeground(AppTheme.TEXT_PRIMARY);
            b.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        }
        return b;
    }
}
