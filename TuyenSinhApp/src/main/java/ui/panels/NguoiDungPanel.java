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

public class NguoiDungPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {
        "ID", "Tài khoản", "Email", "Quyền", "Trạng thái", "Hành động"
    };

    private static final Object[][] DATA = {
        {1, "admin",       "admin@tdtu.edu.vn",       "admin", "enable"},
        {2, "tuyensinh01", "tuyensinh01@tdtu.edu.vn", "user",  "enable"},
        {3, "tuyensinh02", "tuyensinh02@tdtu.edu.vn", "user",  "disable"},
        {4, "tuyensinh03", "tuyensinh03@tdtu.edu.vn", "user",  "enable"},
    };

    public NguoiDungPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnAdd = RoundButton.primary("+ Thêm người dùng");
        btnAdd.addActionListener(e -> showDialog(-1, "add"));

        JPanel topBar = buildTopBar(
            "Quản lý người dùng",
            "Quyền: Quản trị / Nhân viên | Trạng thái: Hoạt động / Tạm khóa",
            btnAdd
        );

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);
        loadData();

        int[] w = {40, 160, 220, 70, 90, 160};
        for (int i = 0; i < w.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // quyen badge
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("admin".equals(v)) { setForeground(AppTheme.PRIMARY); setFont(AppTheme.FONT_BOLD); setText("Quản trị"); }
                else { setForeground(AppTheme.TEXT_SECOND); setFont(AppTheme.FONT_BODY); setText("Nhân viên"); }
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        // trang_thai badge
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("enable".equals(v)) { setForeground(AppTheme.GREEN); setText("✓ Hoạt động"); }
                else { setForeground(AppTheme.RED); setText("✕ Tạm khóa"); }
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

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

    private void loadData() {
        tableModel.setRowCount(0);
        for (Object[] row : DATA) {
            Object[] r = new Object[COLUMNS.length];
            System.arraycopy(row, 0, r, 0, row.length);
            r[COLUMNS.length - 1] = "actions";
            tableModel.addRow(r);
        }
    }

    private void handleAction(int row, MouseEvent e) {
        String trangThai = tableModel.getValueAt(row, 4).toString();
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit     = new JMenuItem("✏ Sửa thông tin");
        JMenuItem password = new JMenuItem("🔑 Đổi password");
        JMenuItem quyen    = new JMenuItem("↕ Đổi quyền Nhân viên ↔ Quản trị");
        JMenuItem toggle   = new JMenuItem("enable".equals(trangThai) ? "🔒 Tạm khóa tài khoản" : "🔓 Mở khóa tài khoản");

        edit.addActionListener(ev -> showDialog(row, "edit"));
        password.addActionListener(ev -> showChangePassword(row));
        quyen.addActionListener(ev -> {
            String cur = tableModel.getValueAt(row, 3).toString();
            String newQ = "admin".equals(cur) ? "user" : "admin";
            String curText = "admin".equals(cur) ? "Quản trị" : "Nhân viên";
            String newText = "admin".equals(newQ) ? "Quản trị" : "Nhân viên";
            int c = JOptionPane.showConfirmDialog(this, "Đổi quyền từ [" + curText + "] → [" + newText + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                tableModel.setValueAt(newQ, row, 3);
                JOptionPane.showMessageDialog(this, "(Demo) Đã cập nhật quyền tài khoản.", "Đã cập nhật", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        toggle.addActionListener(ev -> {
            String newSt = "enable".equals(trangThai) ? "disable" : "enable";
            tableModel.setValueAt(newSt, row, 4);
            table.repaint();
        });

        menu.add(edit);
        menu.add(password);
        menu.addSeparator();
        menu.add(quyen);
        menu.add(toggle);
        menu.show(table, e.getX(), e.getY());
    }

    private void showDialog(int row, String mode) {
        boolean isEdit = "edit".equals(mode);
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa người dùng" : "Thêm người dùng",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16,20,16,20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,5,6,5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        String un  = isEdit ? tableModel.getValueAt(row,1).toString() : "";
        String em  = isEdit ? tableModel.getValueAt(row,2).toString() : "";

        String[][] fields = {
            {"Tài khoản:", un},
            {"Email:", em},
            {"Mật khẩu:", isEdit ? "(giữ nguyên nếu để trống)" : ""},
        };
        JTextField[] tfs = new JTextField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0.35;
            body.add(UIComponents.formLabel(fields[i][0]), gc);
            gc.gridx=1; gc.weightx=0.65;
            tfs[i] = i == 2 ? new JPasswordField(fields[i][1]) : UIComponents.formField(fields[i][1]);
            tfs[i].setFont(AppTheme.FONT_BODY);
            tfs[i].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER), new EmptyBorder(5,8,5,8)));
            body.add(tfs[i], gc);
        }

        gc.gridx=0; gc.gridy=3; gc.weightx=0.35;
        body.add(UIComponents.formLabel("Quyền:"), gc);
        gc.gridx=1; gc.weightx=0.65;
        JComboBox<String> cboQ = UIComponents.comboBox("Nhân viên", "Quản trị");
        if (isEdit && "admin".equals(tableModel.getValueAt(row,3).toString())) cboQ.setSelectedItem("Quản trị");
        body.add(cboQ, gc);

        gc.gridx=0; gc.gridy=4;
        body.add(UIComponents.formLabel("Trạng thái:"), gc);
        gc.gridx=1;
        JComboBox<String> cboSt = UIComponents.comboBox("Hoạt động", "Tạm khóa");
        if (isEdit && "disable".equals(tableModel.getValueAt(row,4).toString())) cboSt.setSelectedItem("Tạm khóa");
        body.add(cboSt, gc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "(Demo) Đã lưu thông tin người dùng.", "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
        });
        footer.add(cancel); footer.add(save);
        d.setLayout(new BorderLayout());
        d.add(body, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showChangePassword(int row) {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Đổi mật khẩu", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(360, 220);
        d.setLocationRelativeTo(this);
        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16,20,16,20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,5,6,5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx=0; gc.gridy=0; gc.weightx=0.4;
        body.add(UIComponents.formLabel("Mật khẩu mới:"), gc);
        gc.gridx=1; gc.weightx=0.6;
        JPasswordField p1 = new JPasswordField();
        p1.setFont(AppTheme.FONT_BODY);
        p1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER), new EmptyBorder(5,8,5,8)));
        body.add(p1, gc);

        gc.gridx=0; gc.gridy=1;
        body.add(UIComponents.formLabel("Xác nhận lại:"), gc);
        gc.gridx=1;
        JPasswordField p2 = new JPasswordField();
        p2.setFont(AppTheme.FONT_BODY);
        p2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER), new EmptyBorder(5,8,5,8)));
        body.add(p2, gc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Đổi mật khẩu");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            String pw1 = new String(p1.getPassword());
            String pw2 = new String(p2.getPassword());
            if (!pw1.equals(pw2)) { JOptionPane.showMessageDialog(d, "Mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
            JOptionPane.showMessageDialog(d, "(Demo) Đã đổi mật khẩu.", "Đã đổi", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
        });
        footer.add(cancel); footer.add(save);
        d.setLayout(new BorderLayout());
        d.add(body, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    static class ActionRenderer extends DefaultTableCellRenderer {
        private JPanel panel;
        public ActionRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 2));
            panel.setOpaque(true);
            JButton e = new JButton("Sửa"); e.setFont(AppTheme.FONT_SMALL);
            e.setBackground(AppTheme.BG_SECONDARY); e.setForeground(AppTheme.TEXT_PRIMARY);
            e.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER)); e.setFocusPainted(false);
            JButton pw = new JButton("Password"); pw.setFont(AppTheme.FONT_SMALL);
            pw.setBackground(AppTheme.AMBER_LIGHT); pw.setForeground(AppTheme.AMBER);
            pw.setBorder(BorderFactory.createLineBorder(AppTheme.AMBER_LIGHT)); pw.setFocusPainted(false);
            JButton d = new JButton("Dis/Enable"); d.setFont(AppTheme.FONT_SMALL);
            d.setBackground(AppTheme.RED_LIGHT); d.setForeground(AppTheme.RED);
            d.setBorder(BorderFactory.createLineBorder(AppTheme.RED_LIGHT)); d.setFocusPainted(false);
            panel.add(e); panel.add(pw); panel.add(d);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            panel.setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
            return panel;
        }
    }
}

