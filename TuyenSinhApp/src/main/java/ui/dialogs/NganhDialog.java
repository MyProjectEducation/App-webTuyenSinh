package ui.dialogs;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class NganhDialog extends JDialog {

    private JTextField txtManganh, txtTennganh, txtNtohopgoc;
    private JTextField txtChitieu, txtDiemsan, txtDiemtrungtuyen;
    private JTextField txtSlXtt, txtSlDgnl, txtSlVsat, txtSlThpt;
    private JCheckBox chkThpt, chkVsat, chkDgnl, chkTuyenthang;
    private boolean isEdit;
    private String manganh;

    public NganhDialog(MainFrame parent, String manganh) {
        super(parent,
              manganh == null ? "Thêm ngành" : "Sửa ngành — " + manganh,
              java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        this.manganh = manganh;
        this.isEdit  = manganh != null;
        setSize(500, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
        if (isEdit) prefillData();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppTheme.BG_PRIMARY);

        // ── Header ───────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(AppTheme.BG_PRIMARY);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
            new EmptyBorder(14, 18, 14, 18)
        ));
        JLabel title = new JLabel(isEdit ? "Sửa ngành tuyển sinh" : "Thêm ngành tuyển sinh");
        title.setFont(AppTheme.FONT_TITLE);
        JLabel sub = new JLabel("Thông tin ngành tuyển sinh");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_SECOND);
        header.add(title);
        header.add(sub);

        // ── Body ─────────────────────────────────────────────────
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(AppTheme.BG_PRIMARY);
        body.setBorder(new EmptyBorder(14, 20, 8, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 6, 5, 6);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        // manganh + n_tohopgoc
        txtManganh  = addField(body, gc, "Mã ngành *:",     0, 0, 0.5);
        txtNtohopgoc = addField(body, gc, "Tổ hợp gốc *:",  0, 2, 0.5);

        // tennganh (full width)
        addLabel(body, gc, "Tên ngành *:", 1, 0);
        txtTennganh = UIComponents.formField();
        gc.gridx = 1; gc.gridy = 1; gc.gridwidth = 3; gc.weightx = 1.0;
        body.add(txtTennganh, gc);
        gc.gridwidth = 1;

        // n_chitieu + n_diemsan
        txtChitieu  = addField(body, gc, "Chỉ tiêu:",          2, 0, 0.5);
        txtDiemsan  = addField(body, gc, "Điểm sàn:",          2, 2, 0.5);

        // n_diemtrungtuyen (full width)
        addLabel(body, gc, "Điểm trúng tuyển:", 3, 0);
        txtDiemtrungtuyen = UIComponents.formField();
        txtDiemtrungtuyen.setToolTipText("Để trống nếu chưa xét tuyển");
        gc.gridx = 1; gc.gridy = 3; gc.gridwidth = 3; gc.weightx = 1.0;
        body.add(txtDiemtrungtuyen, gc);
        gc.gridwidth = 1;

        // ── Phương thức section ───────────────────────────────────
        JLabel lblPt = new JLabel("Phương thức xét tuyển:");
        lblPt.setFont(AppTheme.FONT_SMALL);
        lblPt.setForeground(AppTheme.TEXT_SECOND);
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 4; gc.insets = new Insets(12, 6, 4, 6);
        body.add(lblPt, gc);
        gc.gridwidth = 1; gc.insets = new Insets(5, 6, 5, 6);

        JPanel ptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        ptPanel.setOpaque(false);
        chkThpt      = new JCheckBox("THPT");
        chkVsat      = new JCheckBox("V-SAT 2025");
        chkDgnl      = new JCheckBox("ĐGNL");
        chkTuyenthang = new JCheckBox("Tuyển thẳng");
        for (JCheckBox chk : new JCheckBox[]{chkThpt, chkVsat, chkDgnl, chkTuyenthang}) {
            chk.setFont(AppTheme.FONT_BODY);
            chk.setOpaque(false);
            ptPanel.add(chk);
        }
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 4; gc.weightx = 1.0;
        body.add(ptPanel, gc);
        gc.gridwidth = 1;

        // ── Slot section ──────────────────────────────────────────
        JLabel lblSl = new JLabel("Chỉ tiêu theo phương thức:");
        lblSl.setFont(AppTheme.FONT_SMALL);
        lblSl.setForeground(AppTheme.TEXT_SECOND);
        gc.gridx = 0; gc.gridy = 6; gc.gridwidth = 4; gc.insets = new Insets(12, 6, 4, 6);
        body.add(lblSl, gc);
        gc.gridwidth = 1; gc.insets = new Insets(5, 6, 5, 6);

        txtSlXtt  = addField(body, gc, "Học bạ:", 7, 0, 0.5);
        txtSlDgnl = addField(body, gc, "ĐGNL:",  7, 2, 0.5);
        txtSlVsat = addField(body, gc, "V-SAT:", 8, 0, 0.5);
        txtSlThpt = addField(body, gc, "THPT:",  8, 2, 0.5);

        // Note
        JLabel note = new JLabel("  * Trường bắt buộc");
        note.setFont(AppTheme.FONT_SMALL);
        note.setForeground(AppTheme.TEXT_THIRD);
        gc.gridx = 0; gc.gridy = 9; gc.gridwidth = 4;
        gc.insets = new Insets(8, 6, 0, 6);
        body.add(note, gc);

        JScrollPane scrollBody = new JScrollPane(body);
        scrollBody.setBorder(null);
        scrollBody.getVerticalScrollBar().setUnitIncrement(8);

        // ── Footer ───────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));

        RoundButton btnCancel = RoundButton.secondary("Hủy");
        RoundButton btnSave   = RoundButton.primary(isEdit ? "Cập nhật ngành" : "Lưu ngành");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> doSave());

        footer.add(btnCancel);
        footer.add(btnSave);

        add(header,     BorderLayout.NORTH);
        add(scrollBody, BorderLayout.CENTER);
        add(footer,     BorderLayout.SOUTH);
    }

    private void prefillData() {
        // TODO: load from nganhDAO.findByManganh(manganh)
        switch (manganh) {
            case "7140202":
                txtManganh.setText("7140202");
                txtTennganh.setText("Giáo dục Tiểu học");
                txtNtohopgoc.setText("C01");
                txtChitieu.setText("200");
                txtDiemsan.setText("21.00");
                chkThpt.setSelected(true);
                chkVsat.setSelected(true);
                break;
            case "7140209":
                txtManganh.setText("7140209");
                txtTennganh.setText("Sư phạm Toán học");
                txtNtohopgoc.setText("A00");
                txtChitieu.setText("40");
                txtDiemsan.setText("24.50");
                chkThpt.setSelected(true);
                chkVsat.setSelected(true);
                chkDgnl.setSelected(true);
                break;
            default:
                txtManganh.setText(manganh);
        }
        txtManganh.setEditable(false);
        txtManganh.setBackground(AppTheme.BG_SECONDARY);
    }

    private void doSave() {
        if (txtManganh.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã ngành không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtTennganh.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên ngành không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String info = String.format(
            "(Demo) Lưu ngành tuyển sinh:\n" +
            "- Mã ngành: %s\n" +
            "- Tên ngành: %s\n" +
            "- Tổ hợp gốc: %s\n" +
            "- Chỉ tiêu: %s\n" +
            "- Điểm sàn: %s\n" +
            "- Điểm trúng tuyển: %s\n" +
            "- Phương thức: %s%s%s%s\n" +
            "- Chỉ tiêu học bạ: %s\n" +
            "- Chỉ tiêu ĐGNL: %s\n" +
            "- Chỉ tiêu V-SAT: %s\n" +
            "- Chỉ tiêu THPT: %s",
            txtManganh.getText(),
            txtTennganh.getText(),
            txtNtohopgoc.getText(),
            txtChitieu.getText(),
            txtDiemsan.getText(),
            txtDiemtrungtuyen.getText(),
            chkThpt.isSelected() ? "THPT " : "",
            chkVsat.isSelected() ? "V-SAT " : "",
            chkDgnl.isSelected() ? "ĐGNL " : "",
            chkTuyenthang.isSelected() ? "Tuyển thẳng" : "",
            txtSlXtt.getText(),
            txtSlDgnl.getText(),
            txtSlVsat.getText(),
            txtSlThpt.getText()
        );

        JOptionPane.showMessageDialog(this, info, isEdit ? "Cập nhật thành công" : "Thêm thành công", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private JTextField addField(JPanel panel, GridBagConstraints gc,
                                String label, int row, int col, double weight) {
        addLabel(panel, gc, label, row, col);
        gc.gridx = col + 1; gc.gridy = row; gc.weightx = weight;
        JTextField tf = UIComponents.formField();
        panel.add(tf, gc);
        return tf;
    }

    private void addLabel(JPanel panel, GridBagConstraints gc,
                          String text, int row, int col) {
        gc.gridx = col; gc.gridy = row; gc.weightx = 0.01;
        panel.add(UIComponents.formLabel(text), gc);
    }
}
