package ui.dialogs;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import dao.ThiSinhDAO;
import entity.ThiSinh;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.poi.ss.formula.functions.T;

import java.awt.*;
import java.util.Date;

public class ThiSinhDialog extends JDialog {

    private JTextField txtCccd, txtSbd, txtHo, txtTen, txtNgaySinh;
    private JTextField txtDienThoai, txtEmail, txtNoiSinh, txtDoiTuong;
    private JComboBox<String> cboGioiTinh, cboKhuVuc;
    private JPasswordField txtPassword;
    private boolean isEdit;
    private boolean isviewOnly;
    private String cccd;

    public ThiSinhDialog(MainFrame parent, int row) {
        super(parent, "Chi tiết thí sinh", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        isviewOnly = true;
        setSize(520, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
        showDetailDialog(row);
    }

    public ThiSinhDialog(MainFrame parent, String cccd) {
        super(parent, cccd == null ? "Thêm thí sinh"
                : "Sửa thí sinh — " + cccd,
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        this.cccd = cccd;
        this.isEdit = cccd != null;
        setSize(520, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
        if (isEdit)
            prefillData();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppTheme.BG_PRIMARY);

        // ── Header ───────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_PRIMARY);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                new EmptyBorder(14, 18, 14, 18)));

        JLabel title = new JLabel(
                isviewOnly ? "Thông tin thí sinh" : (isEdit ? "Sửa thông tin thí sinh" : "Thêm thí sinh mới"));

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
        gc.insets = new Insets(5, 6, 5, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        // Row 0: cccd + sobaodanh
        txtCccd = addField(body, gc, "CCCD *:", 0, 0, 0.5);
        txtSbd = addField(body, gc, "Số báo danh:", 0, 2, 0.5);

        // Row 1: ho + ten
        txtHo = addField(body, gc, "Họ *:", 1, 0, 0.5);
        txtTen = addField(body, gc, "Tên *:", 1, 2, 0.5);

        // Row 2: ngay_sinh + gioi_tinh
        addLabel(body, gc, "Ngày sinh:", 2, 0);
        txtNgaySinh = UIComponents.formField();
        txtNgaySinh.setToolTipText("Định dạng: dd/MM/yyyy");
        gc.gridx = 1;
        gc.gridy = 2;
        gc.weightx = 0.5;
        body.add(txtNgaySinh, gc);

        addLabel(body, gc, "Giới tính:", 2, 2);
        cboGioiTinh = UIComponents.comboBox("Nữ", "Nam");
        gc.gridx = 3;
        gc.gridy = 2;
        gc.weightx = 0.5;
        body.add(cboGioiTinh, gc);

        // Row 3: khu_vuc + doi_tuong
        addLabel(body, gc, "Khu vực:", 3, 0);
        cboKhuVuc = UIComponents.comboBox("KV3", "KV2-NT", "KV2", "KV1");
        gc.gridx = 1;
        gc.gridy = 3;
        gc.weightx = 0.5;
        body.add(cboKhuVuc, gc);

        txtDoiTuong = addField(body, gc, "Đối tượng ưu tiên:", 3, 2, 0.5);
        txtDoiTuong.setToolTipText("VD: UT1, UT2, UT3, UT4 — để trống nếu không có");

        // Row 4: dien_thoai + email
        txtDienThoai = addField(body, gc, "Điện thoại:", 4, 0, 0.5);
        txtEmail = addField(body, gc, "Email:", 4, 2, 0.5);

        // Row 5: noi_sinh (full width)
        addLabel(body, gc, "Nơi sinh:", 5, 0);
        txtNoiSinh = UIComponents.formField();
        gc.gridx = 1;
        gc.gridy = 5;
        gc.gridwidth = 3;
        gc.weightx = 1.0;
        body.add(txtNoiSinh, gc);
        gc.gridwidth = 1;

        // Row 6: password (full width)
        addLabel(body, gc, "Mật khẩu:", 6, 0);
        txtPassword = new JPasswordField();
        txtPassword.setFont(AppTheme.FONT_BODY);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(5, 9, 5, 9)));
        if (isEdit)
            txtPassword.setToolTipText("Để trống = giữ nguyên mật khẩu cũ");
        gc.gridx = 1;
        gc.gridy = 6;
        gc.gridwidth = 3;
        gc.weightx = 1.0;
        body.add(txtPassword, gc);
        gc.gridwidth = 1;

        // Note
        JLabel note = new JLabel("  * Trường bắt buộc");
        note.setFont(AppTheme.FONT_SMALL);
        note.setForeground(AppTheme.TEXT_THIRD);
        gc.gridx = 0;
        gc.gridy = 7;
        gc.gridwidth = 4;
        gc.weightx = 1.0;
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
        RoundButton btnSave = RoundButton.primary(isEdit ? "Cập nhật" : "Lưu thí sinh");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> doSave());

        if (isviewOnly) {
        } else {
            footer.add(btnCancel);
            footer.add(btnSave);
        }

        add(header, BorderLayout.NORTH);
        add(scrollBody, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void prefillData() {
        ThiSinh candidate = ThiSinhDAO.getCandidateByCCCD(String.valueOf(cccd));

        txtCccd.setText(cccd);
        txtSbd.setText(candidate.getSobaodanh());
        txtHo.setText(candidate.getHo());
        txtTen.setText(candidate.getTen());
        txtNgaySinh.setText(candidate.getNgaySinh());
        cboGioiTinh.setSelectedItem(candidate.getGioiTinh());
        cboKhuVuc.setSelectedItem(candidate.getKhuVuc());
        txtDoiTuong.setText(candidate.getDoiTuong());
        txtEmail.setText(candidate.getEmail());
        txtDienThoai.setText(candidate.getDienThoai());
        txtNoiSinh.setText(candidate.getNoiSinh());
        txtCccd.setEditable(false);
        txtCccd.setBackground(AppTheme.BG_SECONDARY);
    }

    private void showDetailDialog(int row) {
        ThiSinh candidate = ThiSinhDAO.getCandidateById(row);

        txtCccd.setText(candidate.getCccd());
        txtSbd.setText(candidate.getSobaodanh());
        txtHo.setText(candidate.getHo());
        txtTen.setText(candidate.getTen());
        txtNgaySinh.setText(candidate.getNgaySinh());
        cboGioiTinh.setSelectedItem(candidate.getGioiTinh());
        cboKhuVuc.setSelectedItem(candidate.getKhuVuc());
        txtDoiTuong.setText(candidate.getDoiTuong());
        txtEmail.setText(candidate.getEmail());
        txtDienThoai.setText(candidate.getDienThoai());
        txtNoiSinh.setText(candidate.getNoiSinh());
        txtPassword.setText(candidate.getPassword());
        txtCccd.setBackground(AppTheme.BG_SECONDARY);

        txtSbd.setEditable(false);
        txtSbd.setBackground(AppTheme.BG_SECONDARY);
        txtHo.setEditable(false);
        txtHo.setBackground(AppTheme.BG_SECONDARY);
        txtTen.setEditable(false);
        txtTen.setBackground(AppTheme.BG_SECONDARY);
        txtNgaySinh.setEditable(false);
        txtNgaySinh.setBackground(AppTheme.BG_SECONDARY);
        cboGioiTinh.setEnabled(false);
        cboKhuVuc.setEnabled(false);
        txtDoiTuong.setEditable(false);
        txtDoiTuong.setBackground(AppTheme.BG_SECONDARY);
        txtEmail.setEditable(false);
        txtEmail.setBackground(AppTheme.BG_SECONDARY);
        txtDienThoai.setEditable(false);
        txtDienThoai.setBackground(AppTheme.BG_SECONDARY);
        txtNoiSinh.setEditable(false);
        txtNoiSinh.setBackground(AppTheme.BG_SECONDARY);
        txtPassword.setEditable(false);
        txtPassword.setBackground(AppTheme.BG_SECONDARY);
        txtPassword.setEchoChar((char) 0);
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
            txtHo.requestFocus();
            return;
        }
        if (txtNgaySinh.getText().trim().isEmpty()) {
            showError("Ngày sinh không được để trống!");
            txtNgaySinh.requestFocus();
            return;
        }
        if (txtDienThoai.getText().trim().isEmpty()) {
            showError("Điện thoại không được để trống!");
            txtDienThoai.requestFocus();
            return;
        }
        if (cboGioiTinh.getSelectedItem() == null) {
            showError("Giới tính không được để trống!");
            return;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            showError("Email không được để trống!");
            txtEmail.requestFocus();
            return;
        }
        if (txtNoiSinh.getText().trim().isEmpty()) {
            showError("Nơi sinh không được để trống!");
            txtNoiSinh.requestFocus();
            return;
        }

        // 2. Kiểm tra định dạng chuyên sâu (Nâng cao)

        // Kiểm tra độ dài CCCD (thường là 12 số)
        // String cccd = txtCccd.getText().trim();
        // if (!cccd.matches("\\d{12}")) {
        // showError("CCCD phải bao gồm 12 chữ số!");
        // txtCccd.requestFocus();
        // return;
        // }

        // Kiểm tra định dạng Email
        String email = txtEmail.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Định dạng Email không hợp lệ!");
            txtEmail.requestFocus();
            return;
        }

        // Kiểm tra Số điện thoại (9-11 chữ số)
        String phone = txtDienThoai.getText().trim();
        if (!phone.matches("\\d{9,11}")) {
            showError("Số điện thoại phải từ 9 đến 11 chữ số!");
            txtDienThoai.requestFocus();
            return;
        }

        // Kiểm tra Password (ví dụ tối thiểu 6 ký tự)
        // if (txtPassword.getPassword().length < 6 && txtPassword.getPassword().length
        // > 0) {
        // showError("Mật khẩu phải có ít nhất 6 ký tự!");
        // txtPassword.requestFocus();
        // return;
        // }

        // Kiểm tra CCCD đã tồn tại (chỉ khi thêm mới, không kiểm tra khi đang sửa chính
        // thí sinh đó)
        if (!isEdit) {
            ThiSinh existing = ThiSinhDAO.getCandidateByCCCD(cccd);
            if (existing != null) {
                showError("CCCD đã tồn tại trong hệ thống!");
                txtCccd.requestFocus();
                return;
            }
        }

        // Build info string (in real app → call DAO)
        ThiSinh candidate = new ThiSinh();
        candidate.setCccd(txtCccd.getText().trim());
        candidate.setSobaodanh(txtSbd.getText().trim());
        candidate.setHo(txtHo.getText().trim());
        candidate.setTen(txtTen.getText().trim());
        candidate.setNgaySinh(txtNgaySinh.getText().trim());
        candidate.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
        candidate.setKhuVuc(cboKhuVuc.getSelectedItem().toString());
        candidate.setDoiTuong(txtDoiTuong.getText().trim());
        candidate.setEmail(txtEmail.getText().trim());
        candidate.setDienThoai(txtDienThoai.getText().trim());
        candidate.setNoiSinh(txtNoiSinh.getText().trim());
        if (isEdit) {
            candidate.setIdthisinh(ThiSinhDAO.getCandidateByCCCD(cccd).getIdthisinh());
        }
        if (txtPassword.getPassword().length > 0) {
            candidate.setPassword(new String(txtPassword.getPassword()));
        } else if (isEdit) {
            candidate.setPassword(ThiSinhDAO.getCandidateByCCCD(cccd).getPassword());
        } else {
            candidate.setPassword("123456");
        }

        ThiSinhDAO.updateCandidate(candidate);
        String info = String.format(
                "Lưu thí sinh:\n" +
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
                txtNoiSinh.getText());

        JOptionPane.showMessageDialog(this, info, isEdit ? "Cập nhật thành công" : "Thêm thành công",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
    }

    // Helper: add a label + text field pair in 2-column grid
    private JTextField addField(JPanel panel, GridBagConstraints gc,
            String labelText, int row, int col, double weight) {
        addLabel(panel, gc, labelText, row, col);
        gc.gridx = col + 1;
        gc.gridy = row;
        gc.weightx = weight;
        JTextField tf = UIComponents.formField();
        panel.add(tf, gc);
        return tf;
    }

    private void addLabel(JPanel panel, GridBagConstraints gc,
            String text, int row, int col) {
        gc.gridx = col;
        gc.gridy = row;
        gc.weightx = 0.01;
        JLabel lbl = UIComponents.formLabel(text);
        panel.add(lbl, gc);
    }
}
