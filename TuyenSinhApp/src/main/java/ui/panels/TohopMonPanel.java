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

public class TohopMonPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {
        "idtohop", "matohop", "mon1", "mon2", "mon3", "tentohop", "Hành động"
    };

    private static final Object[][] DATA = {
        {1,  "A00", "TO", "LI",  "HO",  "Toán, Vật lí, Hoá học"},
        {2,  "A01", "TO", "LI",  "N1",  "Toán, Vật lí, Tiếng Anh"},
        {3,  "B00", "TO", "HO",  "SI",  "Toán, Hoá học, Sinh học"},
        {4,  "B03", "TO", "VA",  "SI",  "Toán, Văn, Sinh học"},
        {5,  "C00", "VA", "SU",  "DI",  "Ngữ văn, Lịch sử, Địa lí"},
        {6,  "C01", "TO", "VA",  "LI",  "Toán, Văn, Vật lí"},
        {7,  "C03", "TO", "VA",  "SU",  "Toán, Lịch sử, Ngữ văn"},
        {8,  "C04", "TO", "VA",  "DI",  "Toán, Địa lí, Ngữ văn"},
        {9,  "C19", "VA", "SU",  "GD",  "Văn – Sử – GDCD"},
        {10, "D01", "TO", "VA",  "N1",  "Toán, Tiếng Anh, Ngữ văn"},
        {11, "H00", "VA", "NK3", "NK4", "Ngữ văn, Hình hoạ, Trang trí"},
        {12, "M01", "NK1","NK2", "VA",  "NK1, NK2, Văn"},
        {13, "M02", "TO", "NK1", "NK2", "Toán, Kể chuyện, Đọc diễn cảm, Hát – Nhạc"},
        {14, "N00", "VA", "NK1", "NK2", "Ngữ văn, NK1, NK2"},
        {15, "N01", "VA", "NK5", "NK6", "Ngữ văn, Hát – Nhạc cụ, Xướng âm – Thẩm âm, Tiết tấu"},
    };

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
        loadData();

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
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem("✏ Sửa tổ hợp");
        JMenuItem del  = new JMenuItem("🗑 Xóa");
        edit.addActionListener(ev -> showEditDialog(row));
        del.addActionListener(ev -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Xóa tổ hợp: " + tableModel.getValueAt(row, 1) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) tableModel.removeRow(row);
        });
        menu.add(edit); menu.addSeparator(); menu.add(del);
        menu.show(table, e.getX(), e.getY());
    }

    private void showAddDialog() { buildDialog("Thêm tổ hợp môn", "", "", "", "", ""); }
    private void showEditDialog(int row) {
        buildDialog("Sửa tổ hợp môn",
            tableModel.getValueAt(row, 1).toString(),
            tableModel.getValueAt(row, 2).toString(),
            tableModel.getValueAt(row, 3).toString(),
            tableModel.getValueAt(row, 4).toString(),
            tableModel.getValueAt(row, 5).toString());
    }

    private void buildDialog(String title, String ma, String m1, String m2, String m3, String ten) {
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
            JOptionPane.showMessageDialog(d, "(Demo) Đã lưu tổ hợp môn.", "Đã lưu", JOptionPane.INFORMATION_MESSAGE);
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
