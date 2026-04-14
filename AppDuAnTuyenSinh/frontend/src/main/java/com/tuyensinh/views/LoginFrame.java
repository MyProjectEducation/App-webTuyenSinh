package com.tuyensinh.views;

import com.tuyensinh.models.AppUser;
import com.tuyensinh.services.DataStore;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public LoginFrame() {
        setTitle("Đăng nhập Hệ thống Tuyển sinh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createLoginPanel(), "Login");
        cardPanel.add(createRegisterPanel(), "Register");

        add(cardPanel);
    }

    private JPanel createLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; p.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; p.add(new JLabel("Tài khoản:"), gbc);
        JTextField txtUser = new JTextField(15);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; p.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 2; p.add(new JLabel("Mật khẩu:"), gbc);
        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; p.add(txtPass, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        p.add(btnLogin, gbc);

        JButton btnGoRegister = new JButton("Chưa có tài khoản? Đăng ký User");
        btnGoRegister.setContentAreaFilled(false);
        btnGoRegister.setBorderPainted(false);
        btnGoRegister.setForeground(Color.CYAN);
        gbc.gridy = 4; p.add(btnGoRegister, gbc);

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText(); String pass = new String(txtPass.getPassword());
            Optional<AppUser> found = DataStore.getInstance().appUsers.stream()
                .filter(user -> user.getUsername().equals(u) && user.getPassword().equals(pass))
                .findFirst();
                
            if(found.isPresent()) {
                AppUser user = found.get();
                if(!user.isEnabled()) {
                    JOptionPane.showMessageDialog(this, "Tài khoản của bạn đã bị khóa!"); return;
                }
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công với quyền " + user.getRole() + "!");
                dispose(); // Đóng Login
                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true)); // Mở Main
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnGoRegister.addActionListener(e -> cardLayout.show(cardPanel, "Register"));

        return p;
    }

    private JPanel createRegisterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ĐĂNG KÝ USER", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; p.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; p.add(new JLabel("Họ và Tên:"), gbc);
        JTextField txtName = new JTextField(15); gbc.gridx = 1; p.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; p.add(new JLabel("Tài khoản:"), gbc);
        JTextField txtUser = new JTextField(15); gbc.gridx = 1; p.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 3; p.add(new JLabel("Mật khẩu:"), gbc);
        JPasswordField txtPass = new JPasswordField(15); gbc.gridx = 1; p.add(txtPass, gbc);

        JButton btnReg = new JButton("Đăng ký");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; p.add(btnReg, gbc);

        JButton btnBack = new JButton("Quay lại Đăng nhập");
        btnBack.setContentAreaFilled(false); btnBack.setBorderPainted(false); btnBack.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 5; p.add(btnBack, gbc);

        btnReg.addActionListener(e -> {
            String u = txtUser.getText(); String pass = new String(txtPass.getPassword());
            if(u.isEmpty() || pass.isEmpty()) { JOptionPane.showMessageDialog(this, "Không được để trống!"); return; }
            boolean exists = DataStore.getInstance().appUsers.stream().anyMatch(user -> user.getUsername().equals(u));
            if(exists) { JOptionPane.showMessageDialog(this, "Tài khoản đã tồn tại!"); return; }
            
            AppUser newUser = new AppUser(
                DataStore.getInstance().appUsers.size() + 1, 
                u, pass, txtName.getText(), "USER", true
            );
            DataStore.getInstance().appUsers.add(newUser);
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.");
            cardLayout.show(cardPanel, "Login");
        });

        btnBack.addActionListener(e -> cardLayout.show(cardPanel, "Login"));

        return p;
    }
}
