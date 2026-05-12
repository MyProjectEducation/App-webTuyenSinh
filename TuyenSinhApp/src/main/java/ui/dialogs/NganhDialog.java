package ui.dialogs;

import dao.NganhDAO;
import dao.TohopMonDAO;
import entity.Nganh;
import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.math.BigDecimal;

public class NganhDialog extends JDialog {

    private JTextField txtManganh, txtTennganh;
    private JComboBox<String> cboToHopGoc;
    private JTextField txtChitieu, txtDiemsan, txtDiemtrungtuyen;
    private JTextField txtSlXtt, txtSlDgnl, txtSlVsat, txtSlThpt;
    private JCheckBox chkThpt, chkVsat, chkDgnl, chkTuyenthang;
    private boolean isEdit;
    private String manganh;
    private Runnable onSaved;
    private final NganhDAO nganhDAO = new NganhDAO();
    private final TohopMonDAO tohopMonDAO = new TohopMonDAO();

    public NganhDialog(MainFrame parent, String manganh, Runnable onSaved) {
        super(parent,
              manganh == null ? "Thêm ngành" : "Sửa ngành — " + manganh,
              java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        this.manganh = manganh;
        this.isEdit  = manganh != null;
        this.onSaved = onSaved;
        setSize(740, 620);
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

        addLabel(body, gc, "Tổ hợp gốc *:", 0, 2);
        cboToHopGoc = UIComponents.comboBox("Chọn tổ hợp");
        loadToHopGocOptions(null);
        JButton btnReloadToHop = new JButton("Làm mới");
        btnReloadToHop.setFont(AppTheme.FONT_SMALL);
        btnReloadToHop.setBackground(AppTheme.BG_SECONDARY);
        btnReloadToHop.setForeground(AppTheme.TEXT_PRIMARY);
        btnReloadToHop.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        btnReloadToHop.setFocusPainted(false);
        btnReloadToHop.addActionListener(e -> {
            String current = extractMaToHop(String.valueOf(cboToHopGoc.getSelectedItem()));
            loadToHopGocOptions(current);
        });
        JPanel toHopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        toHopPanel.setOpaque(false);
        toHopPanel.add(cboToHopGoc);
        toHopPanel.add(btnReloadToHop);
        gc.gridx = 3; gc.gridy = 0; gc.weightx = 0.5;
        body.add(toHopPanel, gc);

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
        Nganh nganh = nganhDAO.findByMaNganh(manganh);
        if (nganh == null) return;

        txtManganh.setText(nganh.getMaNganh());
        txtTennganh.setText(nganh.getTenNganh());
        selectToHopGoc(nganh.getToHopGoc());
        txtChitieu.setText(toStr(nganh.getChiTieu()));
        txtDiemsan.setText(toStr(nganh.getDiemSan()));
        txtDiemtrungtuyen.setText(toStr(nganh.getDiemTrungTuyen()));
        chkThpt.setSelected("1".equals(nganh.getThpt()));
        chkVsat.setSelected("1".equals(nganh.getVsat()));
        chkDgnl.setSelected("1".equals(nganh.getDgnl()));
        chkTuyenthang.setSelected("1".equals(nganh.getTuyenThang()));
        txtSlXtt.setText(toStr(nganh.getSlXtt()));
        txtSlDgnl.setText(toStr(nganh.getSlDgnl()));
        txtSlVsat.setText(toStr(nganh.getSlVsat()));
        txtSlThpt.setText(toStr(nganh.getSlThpt()));

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
        String toHopGoc = extractMaToHop(String.valueOf(cboToHopGoc.getSelectedItem()));
        if (!toHopGoc.isEmpty() && toHopGoc.length() > 3) {
            JOptionPane.showMessageDialog(this,
                "Tổ hợp gốc tối đa 3 ký tự (ví dụ: A00, D01).",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Nganh nganh = isEdit ? nganhDAO.findByMaNganh(manganh) : new Nganh();
            if (nganh == null) nganh = new Nganh();

            nganh.setMaNganh(txtManganh.getText().trim());
            nganh.setTenNganh(txtTennganh.getText().trim());
            nganh.setToHopGoc(toHopGoc.isEmpty() ? null : toHopGoc.toUpperCase());
            nganh.setChiTieu(parseInt(txtChitieu.getText()));
            nganh.setDiemSan(parseDecimal(txtDiemsan.getText()));
            nganh.setDiemTrungTuyen(parseDecimal(txtDiemtrungtuyen.getText()));
            nganh.setThpt(chkThpt.isSelected() ? "1" : "0");
            nganh.setVsat(chkVsat.isSelected() ? "1" : "0");
            nganh.setDgnl(chkDgnl.isSelected() ? "1" : "0");
            nganh.setTuyenThang(chkTuyenthang.isSelected() ? "1" : "0");
            nganh.setSlXtt(parseInt(txtSlXtt.getText()));
            nganh.setSlDgnl(parseInt(txtSlDgnl.getText()));
            nganh.setSlVsat(parseInt(txtSlVsat.getText()));
            nganh.setSlThpt(txtSlThpt.getText().trim());

            nganhDAO.saveOrUpdate(nganh);
            if (onSaved != null) onSaved.run();
            JOptionPane.showMessageDialog(this,
                isEdit ? "Đã cập nhật ngành." : "Đã thêm ngành.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không thể lưu ngành. Kiểm tra dữ liệu và DB.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String toStr(Object val) {
        return val == null ? "" : String.valueOf(val);
    }

    private void loadToHopGocOptions(String selectedMaToHop) {
        cboToHopGoc.removeAllItems();
        cboToHopGoc.addItem("Chọn tổ hợp");
        try {
            for (entity.TohopMon t : tohopMonDAO.findAll()) {
                String label = t.getMaToHop() + " - " + t.getTenToHop();
                cboToHopGoc.addItem(label);
                if (selectedMaToHop != null
                    && selectedMaToHop.equalsIgnoreCase(t.getMaToHop())) {
                    cboToHopGoc.setSelectedItem(label);
                }
            }
        } catch (Exception ex) {
            // ignore if DB is unavailable
        }
    }

    private void selectToHopGoc(String maToHop) {
        if (maToHop == null || maToHop.trim().isEmpty()) return;
        for (int i = 0; i < cboToHopGoc.getItemCount(); i++) {
            String item = String.valueOf(cboToHopGoc.getItemAt(i));
            if (maToHop.equalsIgnoreCase(extractMaToHop(item))) {
                cboToHopGoc.setSelectedIndex(i);
                return;
            }
        }
    }

    private String extractMaToHop(String selected) {
        if (selected == null) return "";
        String s = selected.trim();
        if (s.equalsIgnoreCase("Chọn tổ hợp")) return "";
        int idx = s.indexOf(" - ");
        return idx > 0 ? s.substring(0, idx).trim() : s;
    }

    private Integer parseInt(String val) {
        String v = val == null ? "" : val.trim();
        if (v.isEmpty()) return null;
        return Integer.parseInt(v);
    }

    private BigDecimal parseDecimal(String val) {
        String v = val == null ? "" : val.trim();
        if (v.isEmpty()) return null;
        return new BigDecimal(v);
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
