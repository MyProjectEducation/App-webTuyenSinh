package com.tuyensinh.views;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private JPanel cardPanel; // Vùng chứa nội dung chính
    private CardLayout cardLayout; // Layout để chuyển đổi các màn hình
    
    public MainFrame() {
        setTitle("Hệ Thống Quản Lý Tuyển Sinh SGU 2025");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Giữa màn hình
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        // --- 1. Tạo Sidebar (Menu bên trái) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBackground(UIManager.getColor("Panel.background").darker()); // Làm màu nền Sidebar tối hơn một chút
        
        // Tiêu đề Sidebar
        JLabel lblLogo = new JLabel("  SGU ADMISSION", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        sidebarPanel.add(lblLogo);

        // --- 2. Tạo vùng nội dung chính (Card Layout) ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Thêm các Panel thật vào CardLayout
        cardPanel.add(new CandidatePanel(), "Candidates");
        cardPanel.add(new CombinationPanel(), "Combos");
        cardPanel.add(new ProgramPanel(), "Programs");
        cardPanel.add(new ProgramComboPanel(), "ProgramCombos");
        cardPanel.add(new ScorePanel(), "Scores");
        cardPanel.add(new BonusScorePanel(), "BonusScores");
        cardPanel.add(new AspirationPanel(), "Aspirations");
        cardPanel.add(new ConversionPanel(), "Conversions");

        // --- 3. Thêm Nút bấm vào Sidebar ---
        sidebarPanel.add(createMenuButton("Hồ sơ Thí sinh", "Candidates"));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(createMenuButton("Danh sách Ngành", "Programs"));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(createMenuButton("Tổ hợp Xét tuyển", "Combos"));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(createMenuButton("Ngành - Tổ hợp", "ProgramCombos"));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(createMenuButton("Điểm Thí sinh (3 loại)", "Scores"));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(createMenuButton("Điểm Cộng", "BonusScores"));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(createMenuButton("Nguyện vọng Xét tuyển", "Aspirations"));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(createMenuButton("Bảng Quy Đổi Điểm", "Conversions"));

        // Gắn nút Đăng xuất ở cuối Panel Menu
        sidebarPanel.add(Box.createVerticalGlue()); // Đẩy phần tử tiếp theo xuống đáy
        
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setBackground(new Color(255, 69, 58)); // Màu đỏ FlatLaf Dark
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Bạn muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if(opt == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
        
        sidebarPanel.add(btnLogout);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Padding đáy

        // Add vào Frame
        add(sidebarPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }

    // Hàm tiện ích tạo nút Menu
    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        
        // Xử lý sự kiện chuyển trang
        btn.addActionListener(e -> cardLayout.show(cardPanel, cardName));
        return btn;
    }

    // Hàm tiện ích tạo Panel tạm thời để test Layout
    private JPanel createMockPanel(String title, String desc) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        JLabel lblDesc = new JLabel(desc, SwingConstants.CENTER);
        
        panel.add(lblTitle, BorderLayout.CENTER);
        panel.add(lblDesc, BorderLayout.SOUTH);
        return panel;
    }
}
