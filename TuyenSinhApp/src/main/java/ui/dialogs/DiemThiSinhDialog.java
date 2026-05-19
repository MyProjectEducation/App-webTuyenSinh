package ui.dialogs;

import javax.swing.border.EmptyBorder;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;
import dao.DiemThiSinhDAO;
import entity.DiemThiSinh;
import entity.ThiSinh;
import entity.NguyenVong;
import dao.ThiSinhDAO;

import java.awt.*;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class DiemThiSinhDialog {

    private static String formatScore(BigDecimal val) {
        if (val == null) return "0.00";
        return val.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    private static BigDecimal safeParse(String text) {
        if (text == null || text.trim().isEmpty() || text.trim().equalsIgnoreCase("null")) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static void showDetailDialog(MainFrame main, String Cccd) {
        DiemThiSinh diem = DiemThiSinhDAO.getCandidateScoreByCCCD(Cccd);

        ThiSinh candidate = ThiSinhDAO.getCandidateByCCCD(Cccd);

        String cccd = Cccd;
        String fullName = (candidate != null) ? (candidate.getHo() + " " + candidate.getTen()) : "Chưa hoàn thiện hồ sơ";

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
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getTo()) : "0.00"));
        tabTHPT.add(new JLabel("Vật lý:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getLi()) : "0.00"));
        tabTHPT.add(new JLabel("Hóa học:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getHo()) : "0.00"));
        tabTHPT.add(new JLabel("Sinh học:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getSi()) : "0.00"));
        tabTHPT.add(new JLabel("Lịch sử:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getSu()) : "0.00"));
        tabTHPT.add(new JLabel("Địa lý:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getDi()) : "0.00"));
        tabTHPT.add(new JLabel("Ngữ văn:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getVa()) : "0.00"));
        tabTHPT.add(new JLabel("Tin học:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getTi()) : "0.00"));
        tabTHPT.add(new JLabel("Tiếng Anh(thi):"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getN1_thi()) : "0.00"));
        tabTHPT.add(new JLabel("Tiếng Anh(chứng chỉ):"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getN1_cc()) : "0.00"));
        tabTHPT.add(new JLabel("Năng khiếu 1:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getNk1()) : "0.00"));
        tabTHPT.add(new JLabel("Năng khiếu 2:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getNk2()) : "0.00"));
        tabTHPT.add(new JLabel("Công Nghệ cn:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getCncn()) : "0.00"));
        tabTHPT.add(new JLabel("Công nghên nn:"));
        tabTHPT.add(new JLabel(diem != null ? formatScore(diem.getCnnn()) : "0.00"));

        JPanel tabDGNL = new JPanel(new GridLayout(0, 2, 8, 8));
        tabDGNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabDGNL.setBackground(AppTheme.BG_PRIMARY);
        tabDGNL.add(new JLabel("Toán:"));
        tabDGNL.add(new JLabel(diem != null ? formatScore(diem.getTO_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Vật lý:"));
        tabDGNL.add(new JLabel(diem != null ? formatScore(diem.getLI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Hóa học:"));
        tabDGNL.add(new JLabel(diem != null ? formatScore(diem.getHO_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Sinh học:"));
        tabDGNL.add(new JLabel(diem != null ? formatScore(diem.getSI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Lịch sử:"));
        tabDGNL.add(new JLabel(diem != null ? formatScore(diem.getSU_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Địa lý:"));
        tabDGNL.add(new JLabel(diem != null ? formatScore(diem.getDI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Ngữ văn:"));
        tabDGNL.add(new JLabel(diem != null ? formatScore(diem.getVA_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Tiếng Anh:"));
        tabDGNL.add(new JLabel(diem != null ? formatScore(diem.getN1_NL()) : "0.00"));

        JPanel tabVSAT = new JPanel(new GridLayout(0, 2, 8, 8));
        tabVSAT.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabVSAT.setBackground(AppTheme.BG_PRIMARY);
        tabVSAT.add(new JLabel("Toán:"));
        tabVSAT.add(new JLabel(diem != null ? formatScore(diem.getTO_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Ngữ văn:"));
        tabVSAT.add(new JLabel(diem != null ? formatScore(diem.getVA_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Vật lý:"));
        tabVSAT.add(new JLabel(diem != null ? formatScore(diem.getLI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Hóa học:"));
        tabVSAT.add(new JLabel(diem != null ? formatScore(diem.getHO_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Sinh học:"));
        tabVSAT.add(new JLabel(diem != null ? formatScore(diem.getSI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Lịch sử:"));
        tabVSAT.add(new JLabel(diem != null ? formatScore(diem.getSU_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Địa lý:"));
        tabVSAT.add(new JLabel(diem != null ? formatScore(diem.getDI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Tiếng Anh:"));
        tabVSAT.add(new JLabel(diem != null ? formatScore(diem.getN1_VS()) : "0.00"));

        JPanel tabNL = new JPanel(new GridLayout(0, 2, 8, 8));
        tabNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabNL.setBackground(AppTheme.BG_PRIMARY);
        tabNL.add(new JLabel("Điểm ĐGNL:"));
        tabNL.add(new JLabel(diem != null ? formatScore(diem.getNl1()) : "0.00"));

        tabs.addTab("Điểm THPT", tabTHPT);
        tabs.addTab("Điểm V-SAT(M)", tabDGNL);
        tabs.addTab("Điểm V-SAT", tabVSAT);
        tabs.addTab("Điểm ĐGNL", tabNL);

        // Auto-select correct score tab based on registered admission method or active scores
        int selectedTabIndex = 0;
        try (org.hibernate.Session session = util.HibernateUtil.getSessionFactory().openSession()) {
            List<NguyenVong> nvs = session.createQuery("from NguyenVong nv where nv.nnCccd = :cccd", NguyenVong.class)
                .setParameter("cccd", Cccd.trim())
                .list();
            if (nvs != null && !nvs.isEmpty()) {
                for (NguyenVong nv : nvs) {
                    String method = nv.getTtPhuongthuc();
                    if (method != null) {
                        method = method.trim().toUpperCase();
                        if (method.contains("PT3") || method.contains("VSAT")) {
                            selectedTabIndex = 2; // Điểm V-SAT tab index 2
                            break;
                        } else if (method.contains("PT4") || method.contains("DGNL")) {
                            selectedTabIndex = 3; // Điểm ĐGNL tab index 3
                            break;
                        } else if (method.contains("PT2") || method.contains("THPT")) {
                            selectedTabIndex = 0; // Điểm THPT tab index 0
                            break;
                        }
                    }
                }
            } else {
                if (diem != null) {
                    if (diem.getTO_VS() != null && diem.getTO_VS().doubleValue() > 0) {
                        selectedTabIndex = 2;
                    } else if (diem.getTO_NL() != null && diem.getTO_NL().doubleValue() > 0) {
                        selectedTabIndex = 1;
                    } else if (diem.getNl1() != null && diem.getNl1().doubleValue() > 0) {
                        selectedTabIndex = 3;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tabs.setSelectedIndex(selectedTabIndex);

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
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getTo()) : "0.00"));
        tabTHPT.add(new JLabel("Vật lý:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getLi()) : "0.00"));
        tabTHPT.add(new JLabel("Hóa học:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getHo()) : "0.00"));
        tabTHPT.add(new JLabel("Sinh học:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getSi()) : "0.00"));
        tabTHPT.add(new JLabel("Lịch sử:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getSu()) : "0.00"));
        tabTHPT.add(new JLabel("Địa lý:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getDi()) : "0.00"));
        tabTHPT.add(new JLabel("Ngữ văn:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getVa()) : "0.00"));
        tabTHPT.add(new JLabel("Tin học:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getTi()) : "0.00"));
        tabTHPT.add(new JLabel("Tiếng Anh(thi):"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getN1_thi()) : "0.00"));
        tabTHPT.add(new JLabel("Tiếng Anh(chứng chỉ):"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getN1_cc()) : "0.00"));
        tabTHPT.add(new JLabel("Năng khiếu 1:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getNk1()) : "0.00"));
        tabTHPT.add(new JLabel("Năng khiếu 2:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getNk2()) : "0.00"));
        tabTHPT.add(new JLabel("Công Nghệ cn:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getCncn()) : "0.00"));
        tabTHPT.add(new JLabel("Công nghên nn:"));
        tabTHPT.add(new JTextField(row != null ? formatScore(row.getCnnn()) : "0.00"));

        JPanel tabDGNL = new JPanel(new GridLayout(0, 2, 8, 8));
        tabDGNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabDGNL.setBackground(AppTheme.BG_PRIMARY);
        tabDGNL.add(new JLabel("Toán:"));
        tabDGNL.add(new JTextField(row != null ? formatScore(row.getTO_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Vật lý:"));
        tabDGNL.add(new JTextField(row != null ? formatScore(row.getLI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Hóa học:"));
        tabDGNL.add(new JTextField(row != null ? formatScore(row.getHO_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Sinh học:"));
        tabDGNL.add(new JTextField(row != null ? formatScore(row.getSI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Lịch sử:"));
        tabDGNL.add(new JTextField(row != null ? formatScore(row.getSU_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Địa lý:"));
        tabDGNL.add(new JTextField(row != null ? formatScore(row.getDI_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Ngữ văn:"));
        tabDGNL.add(new JTextField(row != null ? formatScore(row.getVA_NL()) : "0.00"));
        tabDGNL.add(new JLabel("Tiếng Anh:"));
        tabDGNL.add(new JTextField(row != null ? formatScore(row.getN1_NL()) : "0.00"));

        JPanel tabVSAT = new JPanel(new GridLayout(0, 2, 8, 8));
        tabVSAT.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabVSAT.setBackground(AppTheme.BG_PRIMARY);
        tabVSAT.add(new JLabel("Toán:"));
        tabVSAT.add(new JTextField(row != null ? formatScore(row.getTO_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Ngữ văn:"));
        tabVSAT.add(new JTextField(row != null ? formatScore(row.getVA_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Vật lý:"));
        tabVSAT.add(new JTextField(row != null ? formatScore(row.getLI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Hóa học:"));
        tabVSAT.add(new JTextField(row != null ? formatScore(row.getHO_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Sinh học:"));
        tabVSAT.add(new JTextField(row != null ? formatScore(row.getSI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Lịch sử:"));
        tabVSAT.add(new JTextField(row != null ? formatScore(row.getSU_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Địa lý:"));
        tabVSAT.add(new JTextField(row != null ? formatScore(row.getDI_VS()) : "0.00"));
        tabVSAT.add(new JLabel("Tiếng Anh:"));
        tabVSAT.add(new JTextField(row != null ? formatScore(row.getN1_VS()) : "0.00"));

        JPanel tabNL = new JPanel(new GridLayout(0, 2, 8, 8));
        tabNL.setBorder(new EmptyBorder(12, 12, 12, 12));
        tabNL.setBackground(AppTheme.BG_PRIMARY);
        tabNL.add(new JLabel("Điểm ĐGNL (0 - 1200):"));
        tabNL.add(new JTextField(row != null ? formatScore(row.getNl1()) : "0.00"));

        tabs.addTab("Điểm THPT", tabTHPT);
        tabs.addTab("Điểm V-SAT(M)", tabDGNL);
        tabs.addTab("Điểm V-SAT", tabVSAT);
        tabs.addTab("Điểm ĐGNL", tabNL);

        // Auto-select correct score tab based on registered admission method or active scores
        int selectedTabIndex = 0;
        if (isEdit) {
            try {
                if (row.getD_phuongthuc() != null) {
                    String method = row.getD_phuongthuc().trim().toUpperCase();
                    if (method.contains("PT3") || method.contains("VSAT")) {
                        selectedTabIndex = 2; // Điểm V-SAT tab
                    } else if (method.contains("PT4") || method.contains("DGNL")) {
                        selectedTabIndex = 3; // Điểm ĐGNL tab
                    } else if (method.contains("PT2") || method.contains("THPT")) {
                        selectedTabIndex = 0; // Điểm THPT tab
                    }
                } else {
                    // Fallback to active scores or aspirations if method not set
                    try (org.hibernate.Session session = util.HibernateUtil.getSessionFactory().openSession()) {
                        List<NguyenVong> nvs = session.createQuery("from NguyenVong nv where nv.nnCccd = :cccd", NguyenVong.class)
                            .setParameter("cccd", Cccd.trim())
                            .list();
                        if (nvs != null && !nvs.isEmpty()) {
                            for (NguyenVong nv : nvs) {
                                String method = nv.getTtPhuongthuc();
                                if (method != null) {
                                    method = method.trim().toUpperCase();
                                    if (method.contains("PT3") || method.contains("VSAT")) {
                                        selectedTabIndex = 2;
                                        break;
                                    } else if (method.contains("PT4") || method.contains("DGNL")) {
                                        selectedTabIndex = 3;
                                        break;
                                    } else if (method.contains("PT2") || method.contains("THPT")) {
                                        selectedTabIndex = 0;
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (row.getTO_VS() != null && row.getTO_VS().doubleValue() > 0) {
                                selectedTabIndex = 2;
                            } else if (row.getTO_NL() != null && row.getTO_NL().doubleValue() > 0) {
                                selectedTabIndex = 1;
                            } else if (row.getNl1() != null && row.getNl1().doubleValue() > 0) {
                                selectedTabIndex = 3;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        tabs.setSelectedIndex(selectedTabIndex);

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
                
                int currentTab = tabs.getSelectedIndex();
                String selectedMethod = "PT2"; // Mặc định THPT
                if (currentTab == 1) {
                    selectedMethod = "PT4"; // V-SAT(M)
                } else if (currentTab == 2) {
                    selectedMethod = "PT3"; // V-SAT
                } else if (currentTab == 3) {
                    selectedMethod = "PT4"; // ĐGNL
                }
                updated.setD_phuongthuc(selectedMethod);
                updated.setTo(safeParse(((JTextField) tabTHPT.getComponent(1)).getText()));
                updated.setLi(safeParse(((JTextField) tabTHPT.getComponent(3)).getText()));
                updated.setHo(safeParse(((JTextField) tabTHPT.getComponent(5)).getText()));
                updated.setSi(safeParse(((JTextField) tabTHPT.getComponent(7)).getText()));
                updated.setSu(safeParse(((JTextField) tabTHPT.getComponent(9)).getText()));
                updated.setDi(safeParse(((JTextField) tabTHPT.getComponent(11)).getText()));
                updated.setVa(safeParse(((JTextField) tabTHPT.getComponent(13)).getText()));
                updated.setTi(safeParse(((JTextField) tabTHPT.getComponent(15)).getText()));
                updated.setN1_thi(safeParse(((JTextField) tabTHPT.getComponent(17)).getText()));
                updated.setN1_cc(safeParse(((JTextField) tabTHPT.getComponent(19)).getText()));
                updated.setNk1(safeParse(((JTextField) tabTHPT.getComponent(21)).getText()));
                updated.setNk2(safeParse(((JTextField) tabTHPT.getComponent(23)).getText()));
                updated.setCncn(safeParse(((JTextField) tabTHPT.getComponent(25)).getText()));
                updated.setCnnn(safeParse(((JTextField) tabTHPT.getComponent(27)).getText()));

                updated.setIddiemthi(row.getIddiemthi());

                updated.setTO_NL(safeParse(((JTextField) tabDGNL.getComponent(1)).getText()));
                updated.setLI_NL(safeParse(((JTextField) tabDGNL.getComponent(3)).getText()));
                updated.setHO_NL(safeParse(((JTextField) tabDGNL.getComponent(5)).getText()));
                updated.setSI_NL(safeParse(((JTextField) tabDGNL.getComponent(7)).getText()));
                updated.setSU_NL(safeParse(((JTextField) tabDGNL.getComponent(9)).getText()));
                updated.setDI_NL(safeParse(((JTextField) tabDGNL.getComponent(11)).getText()));
                updated.setVA_NL(safeParse(((JTextField) tabDGNL.getComponent(13)).getText()));
                updated.setN1_NL(safeParse(((JTextField) tabDGNL.getComponent(15)).getText()));

                updated.setTO_VS(safeParse(((JTextField) tabVSAT.getComponent(1)).getText()));
                updated.setVA_VS(safeParse(((JTextField) tabVSAT.getComponent(3)).getText()));
                updated.setLI_VS(safeParse(((JTextField) tabVSAT.getComponent(5)).getText()));
                updated.setHO_VS(safeParse(((JTextField) tabVSAT.getComponent(7)).getText()));
                updated.setSI_VS(safeParse(((JTextField) tabVSAT.getComponent(9)).getText()));
                updated.setSU_VS(safeParse(((JTextField) tabVSAT.getComponent(11)).getText()));
                updated.setDI_VS(safeParse(((JTextField) tabVSAT.getComponent(13)).getText()));
                updated.setN1_VS(safeParse(((JTextField) tabVSAT.getComponent(15)).getText()));

                java.math.BigDecimal nlVal = safeParse(((JTextField) tabNL.getComponent(1)).getText());
                if (nlVal.compareTo(java.math.BigDecimal.ZERO) < 0 || nlVal.compareTo(java.math.BigDecimal.valueOf(1200)) > 0) {
                    JOptionPane.showMessageDialog(d, "Điểm ĐGNL phải từ 0 đến 1200.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                updated.setNl1(nlVal);
                updated.setNl2(java.math.BigDecimal.ZERO);

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

                int currentTab = tabs.getSelectedIndex();
                String selectedMethod = "PT2"; // Mặc định THPT
                if (currentTab == 1) {
                    selectedMethod = "PT4"; // V-SAT(M)
                } else if (currentTab == 2) {
                    selectedMethod = "PT3"; // V-SAT
                } else if (currentTab == 3) {
                    selectedMethod = "PT4"; // ĐGNL
                }
                newScore.setD_phuongthuc(selectedMethod);

                newScore.setTo(safeParse(((JTextField) tabTHPT.getComponent(1)).getText()));
                newScore.setLi(safeParse(((JTextField) tabTHPT.getComponent(3)).getText()));
                newScore.setHo(safeParse(((JTextField) tabTHPT.getComponent(5)).getText()));
                newScore.setSi(safeParse(((JTextField) tabTHPT.getComponent(7)).getText()));
                newScore.setSu(safeParse(((JTextField) tabTHPT.getComponent(9)).getText()));
                newScore.setDi(safeParse(((JTextField) tabTHPT.getComponent(11)).getText()));
                newScore.setVa(safeParse(((JTextField) tabTHPT.getComponent(13)).getText()));
                newScore.setTi(safeParse(((JTextField) tabTHPT.getComponent(15)).getText()));
                newScore.setN1_thi(safeParse(((JTextField) tabTHPT.getComponent(17)).getText()));
                newScore.setN1_cc(safeParse(((JTextField) tabTHPT.getComponent(19)).getText()));
                newScore.setNk1(safeParse(((JTextField) tabTHPT.getComponent(21)).getText()));
                newScore.setNk2(safeParse(((JTextField) tabTHPT.getComponent(23)).getText()));
                newScore.setCncn(safeParse(((JTextField) tabTHPT.getComponent(25)).getText()));
                newScore.setCnnn(safeParse(((JTextField) tabTHPT.getComponent(27)).getText()));

                newScore.setTO_NL(safeParse(((JTextField) tabDGNL.getComponent(1)).getText()));
                newScore.setLI_NL(safeParse(((JTextField) tabDGNL.getComponent(3)).getText()));
                newScore.setHO_NL(safeParse(((JTextField) tabDGNL.getComponent(5)).getText()));
                newScore.setSI_NL(safeParse(((JTextField) tabDGNL.getComponent(7)).getText()));
                newScore.setSU_NL(safeParse(((JTextField) tabDGNL.getComponent(9)).getText()));
                newScore.setDI_NL(safeParse(((JTextField) tabDGNL.getComponent(11)).getText()));
                newScore.setVA_NL(safeParse(((JTextField) tabDGNL.getComponent(13)).getText()));
                newScore.setN1_NL(safeParse(((JTextField) tabDGNL.getComponent(15)).getText()));

                newScore.setTO_VS(safeParse(((JTextField) tabVSAT.getComponent(1)).getText()));
                newScore.setVA_VS(safeParse(((JTextField) tabVSAT.getComponent(3)).getText()));
                newScore.setLI_VS(safeParse(((JTextField) tabVSAT.getComponent(5)).getText()));
                newScore.setHO_VS(safeParse(((JTextField) tabVSAT.getComponent(7)).getText()));
                newScore.setSI_VS(safeParse(((JTextField) tabVSAT.getComponent(9)).getText()));
                newScore.setSU_VS(safeParse(((JTextField) tabVSAT.getComponent(11)).getText()));
                newScore.setDI_VS(safeParse(((JTextField) tabVSAT.getComponent(13)).getText()));
                newScore.setN1_VS(safeParse(((JTextField) tabVSAT.getComponent(15)).getText()));

                java.math.BigDecimal nlVal = safeParse(((JTextField) tabNL.getComponent(1)).getText());
                if (nlVal.compareTo(java.math.BigDecimal.ZERO) < 0 || nlVal.compareTo(java.math.BigDecimal.valueOf(1200)) > 0) {
                    JOptionPane.showMessageDialog(d, "Điểm ĐGNL phải từ 0 đến 1200.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                newScore.setNl1(nlVal);
                newScore.setNl2(java.math.BigDecimal.ZERO);

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
