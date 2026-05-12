package ui.dialogs;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ThiSinhDialog extends JDialog {

    private JTextField txtCccd, txtSbd, txtHo, txtTen, txtNgaySinh;
    private JTextField txtDienThoai, txtEmail, txtNoiSinh, txtDoiTuong;
    private JComboBox<String> cboGioiTinh, cboKhuVuc;
    private JPasswordField txtPassword;
    private boolean isEdit;
    private String cccd;

    public ThiSinhDialog(MainFrame parent, String cccd) {
        super(parent, cccd == null ? "Thêm thí sinh"
                                   : "Sửa thí sinh — " + cccd,
              java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        this.cccd   = cccd;
        this.isEdit = cccd != null;
        setSize(520, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
        if (isEdit) prefillData();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppTheme.BG_PRIMARY);

        // ── Header ───────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_PRIMARY);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
            new EmptyBorder(14, 18, 14, 18)
        ));
        JLabel title = new JLabel(isEdit ? "Sửa thông tin thí sinh" : "Thêm thí sinh mới");
        title.setFont(AppTheme.FONT_TITLE);
        JLabel sub = new JLabel("Thông tin hồ sơ thí sinh");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_SECOND);
        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(title);
        headerText.add(sub);
        header.add(headerText, BorderLayout.WEST);

        // ── Body (form) ──────────────────────────────────────────
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(AppTheme.BG_PRIMARY);
        body.setBorder(new EmptyBorder(16, 20, 8, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(5, 6, 5, 6);
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.anchor  = GridBagConstraints.WEST;

        // Row 0: cccd + sobaodanh
        txtCccd = addField(body, gc, "CCCD *:", 0, 0, 0.5);
        txtSbd  = addField(body, gc, "Số báo danh:", 0, 2, 0.5);

        // Row 1: ho + ten
        txtHo  = addField(body, gc, "Họ *:", 1, 0, 0.5);
        txtTen = addField(body, gc, "Tên *:", 1, 2, 0.5);

        // Row 2: ngay_sinh + gioi_tinh
        addLabel(body, gc, "Ngày sinh:", 2, 0);
        txtNgaySinh = UIComponents.formField();
        txtNgaySinh.setToolTipText("Định dạng: dd/MM/yyyy");
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 0.5;
        body.add(txtNgaySinh, gc);

        addLabel(body, gc, "Giới tính:", 2, 2);
        cboGioiTinh = UIComponents.comboBox("Nữ", "Nam");
        gc.gridx = 3; gc.gridy = 2; gc.weightx = 0.5;
        body.add(cboGioiTinh, gc);

        // Row 3: khu_vuc + doi_tuong
        addLabel(body, gc, "Khu vực:", 3, 0);
        cboKhuVuc = UIComponents.comboBox("KV3", "KV2-NT", "KV2", "KV1");
        gc.gridx = 1; gc.gridy = 3; gc.weightx = 0.5;
        body.add(cboKhuVuc, gc);

        txtDoiTuong = addField(body, gc, "Đối tượng ưu tiên:", 3, 2, 0.5);
        txtDoiTuong.setToolTipText("VD: UT1, UT2, UT3, UT4 — để trống nếu không có");

        // Row 4: dien_thoai + email
        txtDienThoai = addField(body, gc, "Điện thoại:", 4, 0, 0.5);
        txtEmail     = addField(body, gc, "Email:", 4, 2, 0.5);

        // Row 5: noi_sinh (full width)
        addLabel(body, gc, "Nơi sinh:", 5, 0);
        txtNoiSinh = UIComponents.formField();
        gc.gridx = 1; gc.gridy = 5; gc.gridwidth = 3; gc.weightx = 1.0;
        body.add(txtNoiSinh, gc);
        gc.gridwidth = 1;

        // Row 6: password (full width)
        addLabel(body, gc, "Mật khẩu:", 6, 0);
        txtPassword = new JPasswordField();
        txtPassword.setFont(AppTheme.FONT_BODY);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            new EmptyBorder(5, 9, 5, 9)
        ));
        if (isEdit) txtPassword.setToolTipText("Để trống = giữ nguyên mật khẩu cũ");
        gc.gridx = 1; gc.gridy = 6; gc.gridwidth = 3; gc.weightx = 1.0;
        body.add(txtPassword, gc);
        gc.gridwidth = 1;

        // Note
        JLabel note = new JLabel("  * Trường bắt buộc");
        note.setFont(AppTheme.FONT_SMALL);
        note.setForeground(AppTheme.TEXT_THIRD);
        gc.gridx = 0; gc.gridy = 7; gc.gridwidth = 4; gc.weightx = 1.0;
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
        RoundButton btnSave   = RoundButton.primary(isEdit ? "Cập nhật" : "Lưu thí sinh");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> doSave());

        footer.add(btnCancel);
        footer.add(btnSave);

        add(header,     BorderLayout.NORTH);
        add(scrollBody, BorderLayout.CENTER);
        add(footer,     BorderLayout.SOUTH);
    }

    private void prefillData() {
        // TODO: load from DAO by cccd
        // thiSinhDAO.findByCccd(cccd) → populate fields
        txtCccd.setText(cccd);
        txtSbd.setText(cccd);
        txtHo.setText("Hoàng Văn");
        txtTen.setText("Em");
        txtNgaySinh.setText("27/01/2007");
        cboGioiTinh.setSelectedItem("Nam");
        cboKhuVuc.setSelectedItem("KV1");
        txtDoiTuong.setText("UT3");
        txtEmail.setText("em@email.com");
        txtDienThoai.setText("0905678901");
        txtNoiSinh.setText("Sóc Trăng");
        txtCccd.setEditable(false); // không cho sửa PK
        txtCccd.setBackground(AppTheme.BG_SECONDARY);
    }

    private void doSave() {
        // Validate
        if (txtCccd.getText().trim().isEmpty()) {
            showError("CCCD không được để trống!");
            txtCccd.requestFocus();
            return;
        }
        if (txtHo.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            showError("Họ và tên không được để trống!");
            return;
        }

        // Build info string (in real app → call DAO)
        String info = String.format(
            "(Demo) Lưu thí sinh:\n" +
            "- CCCD: %s\n" +
            "- Số báo danh: %s\n" +
            "- Họ tên: %s %s\n" +
            "- Ngày sinh: %s\n" +
            "- Giới tính: %s\n" +
            "- Khu vực: %s\n" +
            "- Đối tượng: %s\n" +
            "- Email: %s\n" +
            "- Điện thoại: %s\n" +
            "- Nơi sinh: %s",
            txtCccd.getText(),
            txtSbd.getText(),
            txtHo.getText(),
            txtTen.getText(),
            txtNgaySinh.getText(),
            cboGioiTinh.getSelectedItem(),
            cboKhuVuc.getSelectedItem(),
            txtDoiTuong.getText(),
            txtEmail.getText(),
            txtDienThoai.getText(),
            txtNoiSinh.getText()
        );

        JOptionPane.showMessageDialog(this, info, isEdit ? "Cập nhật thành công" : "Thêm thành công", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
    }

    // Helper: add a label + text field pair in 2-column grid
    private JTextField addField(JPanel panel, GridBagConstraints gc,
                                String labelText, int row, int col, double weight) {
        addLabel(panel, gc, labelText, row, col);
        gc.gridx = col + 1; gc.gridy = row; gc.weightx = weight;
        JTextField tf = UIComponents.formField();
        panel.add(tf, gc);
        return tf;
    }

    private void addLabel(JPanel panel, GridBagConstraints gc,
                          String text, int row, int col) {
        gc.gridx = col; gc.gridy = row; gc.weightx = 0.01;
        JLabel lbl = UIComponents.formLabel(text);
        panel.add(lbl, gc);
    }
}
