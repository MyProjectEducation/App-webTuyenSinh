package ui.panels;

import ui.MainFrame;
import dao.TohopMonDAO;
import dao.NganhDAO;
import dao.NganhTohopDAO;
import entity.TohopMon;
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

public class TohopMonPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {
        "idtohop", "matohop", "mon1", "mon2", "mon3", "tentohop", "Hành động"
    };

    private final TohopMonDAO tohopDAO = new TohopMonDAO();
    private final NganhDAO nganhDAO = new NganhDAO();
    private final NganhTohopDAO nganhTohopDAO = new NganhTohopDAO();
    private List<TohopMon> cachedData = new ArrayList<>();

    public TohopMonPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm tổ hợp");
        btnImport.addActionListener(e -> showImport());
        btnAdd.addActionListener(e -> showAddDialog());

        JPanel topBar = buildTopBar(
            "Tổ hợp môn",
            "Danh sách tổ hợp môn xét tuyển",
            btnImport, btnAdd
        );

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        reloadData();

        int[] widths = {55, 80, 60, 60, 60, 300, 100};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Center mon1/2/3
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 2; i <= 4; i++) table.getColumnModel().getColumn(i).setCellRenderer(cr);

        table.getColumn("Hành động").setCellRenderer(new ActionRenderer());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == COLUMNS.length - 1 && row >= 0) handleAction(row, e);
            }
        });

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void reloadData() {
        try {
            cachedData = tohopDAO.findAll();
            fillTable(cachedData);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không thể tải dữ liệu tổ hợp môn. Kiểm tra DB.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillTable(List<TohopMon> data) {
        tableModel.setRowCount(0);
        for (TohopMon t : data) {
            Object[] r = new Object[COLUMNS.length];
            r[0] = t.getId();
            r[1] = t.getMaToHop();
            r[2] = t.getMon1();
            r[3] = t.getMon2();
            r[4] = t.getMon3();
            r[5] = t.getTenToHop();
            r[COLUMNS.length - 1] = "actions";
            tableModel.addRow(r);
        }
    }

    private void handleAction(int row, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa tổ hợp");
        JMenuItem del  = new JMenuItem("🗑 Xóa");
        edit.addActionListener(ev -> showEditDialog(row));
        del.addActionListener(ev -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Xóa tổ hợp: " + tableModel.getValueAt(row, 1) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                try {
                    String maToHop = String.valueOf(tableModel.getValueAt(row, 1));
                    long usedByNganh = nganhDAO.countByToHopGoc(maToHop);
                    long usedByNganhTohop = nganhTohopDAO.countByMaToHop(maToHop);
                    if (usedByNganh > 0 || usedByNganhTohop > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Không thể xóa tổ hợp " + maToHop + ". "
                                + "Đang được dùng bởi Ngành (" + usedByNganh + ") "
                                + "và Ngành-Tổ hợp (" + usedByNganhTohop + ").\n"
                                + "Vui lòng xóa các liên quan trước.",
                            "Ràng buộc dữ liệu", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Integer id = Integer.valueOf(tableModel.getValueAt(row, 0).toString());
                    tohopDAO.deleteById(id);
                    reloadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa tổ hợp. Kiểm tra ràng buộc DB.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menu.add(edit); menu.addSeparator(); menu.add(del);
        menu.show(table, e.getX(), e.getY());
    }

    private void showAddDialog() { buildDialog("Thêm tổ hợp môn", null); }
    private void showEditDialog(int row) {
        Integer id = Integer.valueOf(tableModel.getValueAt(row, 0).toString());
        TohopMon tohop = tohopDAO.findById(id);
        buildDialog("Sửa tổ hợp môn", tohop);
    }

    private void buildDialog(String title, TohopMon tohop) {
        boolean isEdit = tohop != null && tohop.getId() != null;
        String ma = isEdit ? tohop.getMaToHop() : "";
        String m1 = isEdit ? tohop.getMon1() : "";
        String m2 = isEdit ? tohop.getMon2() : "";
        String m3 = isEdit ? tohop.getMon3() : "";
        String ten = isEdit ? tohop.getTenToHop() : "";

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(420, 320);
        d.setLocationRelativeTo(this);
        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16, 20, 16, 20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Mã tổ hợp:", "Môn 1:", "Môn 2:", "Môn 3:", "Tên tổ hợp:"};
        String[] vals   = {ma, m1, m2, m3, ten};
        JTextField[] fields = new JTextField[5];
        for (int i = 0; i < labels.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0.3;
            body.add(UIComponents.formLabel(labels[i]), gc);
            gc.gridx=1; gc.weightx=0.7;
            fields[i] = UIComponents.formField(vals[i]);
            body.add(fields[i], gc);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            try {
                TohopMon entity = isEdit ? tohop : new TohopMon();
                entity.setMaToHop(fields[0].getText().trim());
                entity.setMon1(fields[1].getText().trim());
                entity.setMon2(fields[2].getText().trim());
                entity.setMon3(fields[3].getText().trim());
                entity.setTenToHop(fields[4].getText().trim());
                tohopDAO.saveOrUpdate(entity);
                reloadData();
                JOptionPane.showMessageDialog(d, "Đã lưu tổ hợp môn.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d,
                    "Không thể lưu tổ hợp. Kiểm tra dữ liệu và DB.",
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
        fc.setDialogTitle("Import — Tổ hợp môn");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx)", "xlsx","xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            JOptionPane.showMessageDialog(this, "(Demo) Sẽ import danh sách tổ hợp môn.", "Import", JOptionPane.INFORMATION_MESSAGE);
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
