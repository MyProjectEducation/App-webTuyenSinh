package ui;

import ui.components.AppTheme;
import ui.components.UIComponents;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Đăng nhập - Hệ thống Tuyển sinh SGU 2026");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(AppTheme.BG_TERTIARY);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(AppTheme.BG_TERTIARY);
        setContentPane(root);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BG_PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(AppTheme.BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        card.setPreferredSize(new Dimension(360, 420));

        // Logo icon
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoRow.setOpaque(false);
        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(Color.WHITE);
                int w = getWidth(), h = getHeight();
                g2.fillRoundRect(w/6, h/6, w*2/6, h*2/6, 3, 3);
                g2.setColor(new Color(255,255,255,150));
                g2.fillRoundRect(w*3/6+2, h/6, w*2/6, h*2/6, 3, 3);
                g2.fillRoundRect(w/6, h*3/6+2, w*2/6, h*2/6, 3, 3);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(w*3/6+2, h*3/6+2, w*2/6, h*2/6, 3, 3);
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(48, 48));
        iconBox.setOpaque(false);
        logoRow.add(iconBox);
        card.add(logoRow);
        card.add(Box.createVerticalStrut(14));

        // Title
        JLabel title = new JLabel("Hệ thống Tuyển sinh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        JLabel sub = new JLabel("SGU Admin 2026");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECOND);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(28));

        // Username
        JLabel lblUser = UIComponents.formLabel("Tên đăng nhập");
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblUser);
        card.add(Box.createVerticalStrut(4));

        txtUsername = new JTextField("admin");
        txtUsername.setFont(AppTheme.FONT_BODY);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            new EmptyBorder(7, 10, 7, 10)
        ));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(12));

        // Password
        JLabel lblPass = UIComponents.formLabel("Mật khẩu");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblPass);
        card.add(Box.createVerticalStrut(4));

        txtPassword = new JPasswordField("password");
        txtPassword.setFont(AppTheme.FONT_BODY);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            new EmptyBorder(7, 10, 7, 10)
        ));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(22));

        // Login button
        UIComponents.RoundButton btnLogin = UIComponents.RoundButton.primary("Đăng nhập");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> doLogin());
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(16));

        // Footer
        JLabel footer = new JLabel("Swing + Hibernate + MySQL  •  SGU © 2026");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(AppTheme.TEXT_THIRD);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(footer);

        root.add(card);

        // Enter key
        getRootPane().setDefaultButton(btnLogin);
    }

    private void doLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // TODO: validate against DB via Hibernate
        // Temporarily accept any non-empty credentials
        MainFrame mainFrame = new MainFrame(user);
        mainFrame.setVisible(true);
        this.dispose();
    }
}
