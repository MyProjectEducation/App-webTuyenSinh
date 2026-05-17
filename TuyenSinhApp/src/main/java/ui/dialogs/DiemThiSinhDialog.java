package ui.dialogs;

import javax.swing.border.EmptyBorder;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import dao.DiemThiSinhDAO;
import entity.DiemThiSinh;
import entity.ThiSinh;
import dao.ThiSinhDAO;

import java.awt.*;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class DiemThiSinhDialog {

    public static void showDetailDialog(MainFrame main, String Cccd) {
        DiemThiSinh diem = DiemThiSinhDAO.getCandidateScoreByCCCD(Cccd);

        ThiSinh candidate = ThiSinhDAO.getCandidateByCCCD(Cccd);

        String cccd = Cccd;
        String fullName = candidate.getHo() + " " + candidate.getTen();

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(main),
                "Chi tiết thí sinh", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(560, 420);
        d.setLocationRelativeTo(main);

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 14, 10, 14));
        header.setBackground(AppTheme.BG_PRIMARY);
        JLabel title = new JLabel(fullName + " — " + cccd);
        title.setFont(AppTheme.FONT_BOLD);
        JLabel sub = new JLabel("Xem thông tin và điểm của thí sinh ");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_SECOND);
        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY);

        JPanel tabTHPT = new JPanel(new GridLayout(0, 2, 8, 8));
        tabTHPT.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabTHPT.setBackground(AppTheme.BG_PRIMARY);
        tabTHPT.add(new JLabel("Toán:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getTo() + "" : "0.00"));
        tabTHPT.add(new JLabel("Vật lý:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getLi() + "" : "0.00"));
        tabTHPT.add(new JLabel("Hóa học:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getHo() + "" : "0.00"));
        tabTHPT.add(new JLabel("Sinh học:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getSi() + "" : "0.00"));
        tabTHPT.add(new JLabel("Lịch sử:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getSu() + "" : "0.00"));
        tabTHPT.add(new JLabel("Địa lý:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getDi() + "" : "0.00"));
        tabTHPT.add(new JLabel("Ngữ văn:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getVa() + "" : "0.00"));
        tabTHPT.add(new JLabel("Tin học:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getTi() + "" : "0.00"));
        tabTHPT.add(new JLabel("Tiếng Anh(thi):"));
        tabTHPT.add(new JLabel(diem != null ? diem.getN1_thi() + "" : "0.00"));
        tabTHPT.add(new JLabel("Tiếng Anh(chứng chỉ):"));
        tabTHPT.add(new JLabel(diem != null ? diem.getN1_cc() + "" : "0.00"));
        tabTHPT.add(new JLabel("Năng khiếu 1:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getNk1() + "" : "0.00"));
        tabTHPT.add(new JLabel("Năng khiếu 2:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getNk2() + "" : "0.00"));
        tabTHPT.add(new JLabel("Công Nghệ cn:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getCncn() + "" : "0.00"));
        tabTHPT.add(new JLabel("Công nghên nn:"));
        tabTHPT.add(new JLabel(diem != null ? diem.getCnnn() + "" : "0.00"));

        JPanel tabDGNL = new JPanel(new GridLayout(0, 2, 8, 8));
        tabDGNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabDGNL.setBackground(AppTheme.BG_PRIMARY);
        tabDGNL.add(new JLabel("Toán:"));
        tabDGNL.add(new JLabel(diem != null ? diem.getTO_NL() + "" : "0.00"));
        tabDGNL.add(new JLabel("Vật lý:"));
        tabDGNL.add(new JLabel(diem != null ? diem.getLI_NL() + "" : "0.00"));
        tabDGNL.add(new JLabel("Hóa học:"));
        tabDGNL.add(new JLabel(diem != null ? diem.getHO_NL() + "" : "0.00"));
        tabDGNL.add(new JLabel("Sinh học:"));
        tabDGNL.add(new JLabel(diem != null ? diem.getSI_NL() + "" : "0.00"));
        tabDGNL.add(new JLabel("Lịch sử:"));
        tabDGNL.add(new JLabel(diem != null ? diem.getSU_NL() + "" : "0.00"));
        tabDGNL.add(new JLabel("Địa lý:"));
        tabDGNL.add(new JLabel(diem != null ? diem.getDI_NL() + "" : "0.00"));
        tabDGNL.add(new JLabel("Ngữ văn:"));
        tabDGNL.add(new JLabel(diem != null ? diem.getVA_NL() + "" : "0.00"));
        tabDGNL.add(new JLabel("Tiếng Anh:"));
        tabDGNL.add(new JLabel(diem != null ? diem.getN1_NL() + "" : "0.00"));

        JPanel tabVSAT = new JPanel(new GridLayout(0, 2, 8, 8));
        tabVSAT.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabVSAT.setBackground(AppTheme.BG_PRIMARY);
        tabVSAT.add(new JLabel("Toán:"));
        tabVSAT.add(new JLabel(diem != null ? diem.getTO_VS() + "" : "0.00"));
        tabVSAT.add(new JLabel("Ngữ văn:"));
        tabVSAT.add(new JLabel(diem != null ? diem.getVA_VS() + "" : "0.00"));
        tabVSAT.add(new JLabel("Vật lý:"));
        tabVSAT.add(new JLabel(diem != null ? diem.getLI_VS() + "" : "0.00"));
        tabVSAT.add(new JLabel("Hóa học:"));
        tabVSAT.add(new JLabel(diem != null ? diem.getHO_VS() + "" : "0.00"));
        tabVSAT.add(new JLabel("Sinh học:"));
        tabVSAT.add(new JLabel(diem != null ? diem.getSI_VS() + "" : "0.00"));
        tabVSAT.add(new JLabel("Lịch sử:"));
        tabVSAT.add(new JLabel(diem != null ? diem.getSU_VS() + "" : "0.00"));
        tabVSAT.add(new JLabel("Địa lý:"));
        tabVSAT.add(new JLabel(diem != null ? diem.getDI_VS() + "" : "0.00"));

        JPanel tabNL = new JPanel(new GridLayout(2, 2, 8, 8));
        tabNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabNL.setBackground(AppTheme.BG_PRIMARY);
        tabNL.add(new JLabel("Năng lực Dợt 1:"));
        tabNL.add(new JLabel(diem != null ? diem.getNl1() + "" : "0.00"));
        tabNL.add(new JLabel("Năng lực Dợt 2:"));
        tabNL.add(new JLabel(diem != null ? diem.getNl2() + "" : "0.00"));

        tabs.addTab("Điểm THPT", tabTHPT);
        tabs.addTab("Điểm V-SAT(M)", tabDGNL);
        tabs.addTab("Điểm V-SAT", tabVSAT);
        tabs.addTab("Điểm NL", tabNL);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                AppTheme.BORDER));
        RoundButton close = RoundButton.secondary("Đóng");
        close.addActionListener(e -> d.dispose());
        footer.add(close);

        d.setLayout(new BorderLayout());
        d.add(header, BorderLayout.NORTH);
        d.add(tabs, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    public static DiemThiSinh showDialog(MainFrame main, String Cccd) {
        DiemThiSinh row = DiemThiSinhDAO.getCandidateScoreByCCCD(Cccd);
        DiemThiSinh newScore = new DiemThiSinh();
        boolean isEdit = row != null;
        JDialog d = new JDialog(main,
                isEdit ? "Sửa điểm thi" : "Thêm điểm thi",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(520, 680);
        d.setLocationRelativeTo(main);

        String cccd = isEdit ? row.getCccd() : "";
        String sbd = isEdit ? row.getSobaodanh() : "";
        JTextField txtCccd = new JTextField(12);
        JTextField txtSbd = new JTextField(10);

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 14, 10, 14));
        header.setBackground(AppTheme.BG_PRIMARY);
        JLabel sub = new JLabel(isEdit ? "Cập nhật điểm thi cho thí sinh" : "Nhập điểm thi cho thí sinh");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_SECOND);

        JComponent titleComponent;

        if (isEdit) {
            JLabel lblTitle = new JLabel("SBD: " + sbd + " | CCCD: " + cccd);
            lblTitle.setFont(AppTheme.FONT_BOLD);
            titleComponent = lblTitle;
        } else {
            // Tạo một panel phụ xếp hàng ngang để chứa các ô nhập liệu
            JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            panelInput.setBackground(AppTheme.BG_PRIMARY); // Giữ màu nền đồng bộ

            JLabel lblSbd = new JLabel("SBD*: ");
            lblSbd.setFont(AppTheme.FONT_BOLD);

            JLabel lblCccd = new JLabel(" - CCCD*: ");
            lblCccd.setFont(AppTheme.FONT_BOLD);

            // Add tuần tự vào panel theo hàng ngang
            panelInput.add(lblSbd);
            panelInput.add(txtSbd);
            panelInput.add(lblCccd);
            panelInput.add(txtCccd);

            titleComponent = panelInput;
        }
        titleComponent.setFont(AppTheme.FONT_BOLD);
        header.add(titleComponent, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY);

        JPanel tabTHPT = new JPanel(new GridLayout(0, 2, 8, 8));
        tabTHPT.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabTHPT.setBackground(AppTheme.BG_PRIMARY);
        tabTHPT.add(new JLabel("Toán:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getTo()) : "0.00"));
        tabTHPT.add(new JLabel("Vật lý:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getLi()) : "0.00"));
        tabTHPT.add(new JLabel("Hóa học:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getHo()) : "0.00"));
        tabTHPT.add(new JLabel("Sinh học:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getSi()) : "0.00"));
        tabTHPT.add(new JLabel("Lịch sử:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getSu()) : "0.00"));
        tabTHPT.add(new JLabel("Địa lý:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getDi()) : "0.00"));
        tabTHPT.add(new JLabel("Ngữ văn:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getVa()) : "0.00"));
        tabTHPT.add(new JLabel("Tin học:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getTi()) : "0.00"));
        tabTHPT.add(new JLabel("Tiếng Anh(thi):"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getN1_thi()) : "0.00"));
        tabTHPT.add(new JLabel("Tiếng Anh(chứng chỉ):"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getN1_cc()) : "0.00"));
        tabTHPT.add(new JLabel("Năng khiếu 1:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getNk1()) : "0.00"));
        tabTHPT.add(new JLabel("Năng khiếu 2:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getNk2()) : "0.00"));
        tabTHPT.add(new JLabel("Công Nghệ cn:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getCncn()) : "0.00"));
        tabTHPT.add(new JLabel("Công nghên nn:"));
        tabTHPT.add(new JTextField(row != null ? String.valueOf(row.getCnnn()) : "0.00"));

        JPanel tabDGNL = new JPanel(new GridLayout(0, 2, 8, 8));
        tabDGNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabDGNL.setBackground(AppTheme.BG_PRIMARY);
        tabDGNL.add(new JLabel("Toán:"));
        tabDGNL.add(new JTextField(row != null ? String.valueOf(row.getTO_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Vật lý:"));
        tabDGNL.add(new JTextField(row != null ? String.valueOf(row.getLI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Hóa học:"));
        tabDGNL.add(new JTextField(row != null ? String.valueOf(row.getHO_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Sinh học:"));
        tabDGNL.add(new JTextField(row != null ? String.valueOf(row.getSI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Lịch sử:"));
        tabDGNL.add(new JTextField(row != null ? String.valueOf(row.getSU_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Địa lý:"));
        tabDGNL.add(new JTextField(row != null ? String.valueOf(row.getDI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Ngữ văn:"));
        tabDGNL.add(new JTextField(row != null ? String.valueOf(row.getVA_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Tiếng Anh:"));
        tabDGNL.add(new JTextField(row != null ? String.valueOf(row.getN1_NL()) : "0.00"));

        JPanel tabVSAT = new JPanel(new GridLayout(0, 2, 8, 8));
        tabVSAT.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabVSAT.setBackground(AppTheme.BG_PRIMARY);
        tabVSAT.add(new JLabel("Toán:"));
        tabVSAT.add(new JTextField(row != null ? String.valueOf(row.getTO_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Ngữ văn:"));
        tabVSAT.add(new JTextField(row != null ? String.valueOf(row.getVA_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Vật lý:"));
        tabVSAT.add(new JTextField(row != null ? String.valueOf(row.getLI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Hóa học:"));
        tabVSAT.add(new JTextField(row != null ? String.valueOf(row.getHO_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Sinh học:"));
        tabVSAT.add(new JTextField(row != null ? String.valueOf(row.getSI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Lịch sử:"));
        tabVSAT.add(new JTextField(row != null ? String.valueOf(row.getSU_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Địa lý:"));
        tabVSAT.add(new JTextField(row != null ? String.valueOf(row.getDI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Tiếng Anh:"));
        tabVSAT.add(new JTextField(row != null ? String.valueOf(row.getN1_VS()) : "0.00"));

        JPanel tabNL = new JPanel(new GridLayout(0, 2, 8, 8));
        tabNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabNL.setBackground(AppTheme.BG_PRIMARY);
        tabNL.add(new JLabel("Năng lực Dợt 1:"));
        tabNL.add(new JTextField(row != null ? String.valueOf(row.getNl1()) : "0.00"));
        tabNL.add(new JLabel("Năng lực Dợt 2:"));
        tabNL.add(new JTextField(row != null ? String.valueOf(row.getNl2()) : "0.00"));

        tabs.addTab("Điểm THPT", tabTHPT);
        tabs.addTab("Điểm ĐGNL", tabDGNL);
        tabs.addTab("Điểm V-SAT", tabVSAT);
        tabs.addTab("Điểm NL", tabNL);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save = RoundButton.primary("Lưu điểm");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            if (isEdit) {
                DiemThiSinh updated = new DiemThiSinh();
                updated.setCccd(cccd);
                updated.setSobaodanh(sbd);
                updated.setTo(BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(1)).getText())));
                updated.setLi(BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(3)).getText())));
                updated.setHo(BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(5)).getText())));
                updated.setSi(BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(7)).getText())));
                updated.setSu(BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(9)).getText())));
                updated.setDi(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(11)).getText())));
                updated.setVa(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(13)).getText())));
                updated.setTi(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(15)).getText())));
                updated.setN1_thi(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(17)).getText())));
                updated.setN1_cc(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(19)).getText())));
                updated.setNk1(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(21)).getText())));
                updated.setNk2(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(23)).getText())));
                updated.setCncn(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(25)).getText())));
                updated.setCnnn(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(27)).getText())));

                updated.setIddiemthi(row.getIddiemthi());

                updated.setTO_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(1)).getText())));
                updated.setLI_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(3)).getText())));
                updated.setHO_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(5)).getText())));
                updated.setSI_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(7)).getText())));
                updated.setSU_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(9)).getText())));
                updated.setDI_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(11)).getText())));
                updated.setVA_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(13)).getText())));
                updated.setN1_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(15)).getText())));

                updated.setTO_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(1)).getText())));
                updated.setVA_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(3)).getText())));
                updated.setLI_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(5)).getText())));
                updated.setHO_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(7)).getText())));
                updated.setSI_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(9)).getText())));
                updated.setSU_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(11)).getText())));
                updated.setDI_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(13)).getText())));
                updated.setN1_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(15)).getText())));

                updated.setNl1(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabNL.getComponent(1)).getText())));
                updated.setNl2(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabNL.getComponent(3)).getText())));

                DiemThiSinhDAO.updateCandidateScore(updated);

            } else {
                if (txtCccd.getText().isEmpty() || txtSbd.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(d, "Vui lòng nhập đầy đủ SBD và CCCD.", "Thiếu thông tin",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (ThiSinhDAO.getCandidateByCCCD(txtCccd.getText()) == null) {
                    JOptionPane.showMessageDialog(d, "Không tìm thấy thí sinh với CCCD đã nhập.",
                            "Thông tin không hợp lệ",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (DiemThiSinhDAO.getCandidateScoreByCCCD(txtCccd.getText()) != null) {
                    JOptionPane.showMessageDialog(d, "Đã tồn tại điểm thi cho CCCD này. Vui lòng kiểm tra lại.",
                            "Thông tin trùng lặp",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                newScore.setCccd(txtCccd.getText());
                newScore.setSobaodanh(txtSbd.getText());

                newScore.setTo(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(1)).getText())));
                newScore.setLi(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(3)).getText())));
                newScore.setHo(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(5)).getText())));
                newScore.setSi(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(7)).getText())));
                newScore.setSu(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(9)).getText())));
                newScore.setDi(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(11)).getText())));
                newScore.setVa(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(13)).getText())));
                newScore.setTi(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(15)).getText())));
                newScore.setN1_thi(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(17)).getText())));
                newScore.setN1_cc(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(19)).getText())));
                newScore.setNk1(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(21)).getText())));
                newScore.setNk2(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(23)).getText())));
                newScore.setCncn(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(25)).getText())));
                newScore.setCnnn(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabTHPT.getComponent(27)).getText())));

                newScore.setTO_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(1)).getText())));
                newScore.setLI_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(3)).getText())));
                newScore.setHO_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(5)).getText())));
                newScore.setSI_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(7)).getText())));
                newScore.setSU_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(9)).getText())));
                newScore.setDI_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(11)).getText())));
                newScore.setVA_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(13)).getText())));
                newScore.setN1_NL(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabDGNL.getComponent(15)).getText())));

                newScore.setTO_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(1)).getText())));
                newScore.setVA_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(3)).getText())));
                newScore.setLI_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(5)).getText())));
                newScore.setHO_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(7)).getText())));
                newScore.setSI_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(9)).getText())));
                newScore.setSU_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(11)).getText())));
                newScore.setDI_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(13)).getText())));
                newScore.setN1_VS(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabVSAT.getComponent(15)).getText())));

                newScore.setNl1(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabNL.getComponent(1)).getText())));
                newScore.setNl2(
                        BigDecimal.valueOf(Double.parseDouble(((JTextField) tabNL.getComponent(3)).getText())));

                DiemThiSinhDAO.createCandidateScore(newScore);
            }
            d.dispose();
        });
        footer.add(cancel);
        footer.add(save);
        d.setLayout(new BorderLayout());
        d.add(header, BorderLayout.NORTH);
        d.add(tabs, BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);

        return newScore;
    }

}
