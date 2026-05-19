package ui;

import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.panels.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private JPanel contentArea;
    private CardLayout cardLayout;
    private Map<String, JPanel> panelMap = new LinkedHashMap<>();
    private String currentPanel = "";
    private JPanel sidebarPanel;
    private String username;

    public MainFrame(String username) {
        this.username = username;
        setTitle("Hệ thống Tuyển sinh TDTU 2025 — " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setMinimumSize(new Dimension(1100, 640));
        setLocationRelativeTo(null);
        buildUI();
        showPanel("dashboard");
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG_TERTIARY);
        setContentPane(root);

        // Sidebar
        sidebarPanel = buildSidebar();
        root.add(sidebarPanel, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(AppTheme.BG_TERTIARY);
        root.add(contentArea, BorderLayout.CENTER);

        // Register all panels
        registerPanel("dashboard",   new DashboardPanel(this));
        registerPanel("thisinh",     new ThiSinhPanel(this));
        registerPanel("nganh",       new NganhPanel(this));
        registerPanel("tohopmon",    new TohopMonPanel(this));
        registerPanel("nganhtohop",  new NganhTohopPanel(this));
        registerPanel("diem",        new DiemThiPanel(this));
        registerPanel("diemcong",    new DiemCongPanel(this));
        registerPanel("nguyenvong",  new NguyenVongPanel(this));
        registerPanel("xettuyen",    new XetTuyenPanel(this));
        registerPanel("bangquydoi",  new BangQuyDoiPanel(this));
        registerPanel("nguoidung",   new NguoiDungPanel(this));
    }

    private void registerPanel(String key, JPanel panel) {
        panelMap.put(key, panel);
        contentArea.add(panel, key);
    }

    public void showPanel(String key) {
        currentPanel = key;
        cardLayout.show(contentArea, key);
        refreshSidebarHighlight();
        
        if ("dashboard".equals(key)) {
            JPanel panel = panelMap.get(key);
            if (panel instanceof DashboardPanel) {
                ((DashboardPanel) panel).reloadData();
            }
        }
    }

    // ─── Sidebar ──────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel(new BorderLayout());
        sb.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 0));
        sb.setBackground(AppTheme.SIDEBAR_BG);
        sb.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER));

        // Logo header
        JPanel logoPanel = buildLogoPanel();
        sb.add(logoPanel, BorderLayout.NORTH);

        // Nav scroll
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setBackground(AppTheme.SIDEBAR_BG);
        navContainer.setBorder(new EmptyBorder(8, 8, 8, 8));

        addNavSection(navContainer, "TỔNG QUAN");
        addNavItem(navContainer, "dashboard",   "Dashboard",          "▦");
        addNavSection(navContainer, "QUẢN LÝ DỮ LIỆU");
        addNavItem(navContainer, "thisinh",     "Thí sinh",           "◉");
        addNavItem(navContainer, "nganh",       "Ngành tuyển sinh",   "⌂");
        addNavItem(navContainer, "tohopmon",    "Tổ hợp môn",        "≡");
        addNavItem(navContainer, "nganhtohop",  "Ngành – Tổ hợp",    "⊞");
        addNavItem(navContainer, "diem",        "Điểm thi",           "∿");
        addNavItem(navContainer, "diemcong",    "Điểm cộng",          "⊕");
        addNavSection(navContainer, "XÉT TUYỂN");
        addNavItem(navContainer, "nguyenvong",  "Nguyện vọng",        "☰");
        addNavItem(navContainer, "xettuyen",    "Kết quả xét tuyển",  "✓");
        addNavItem(navContainer, "bangquydoi",  "Bảng quy đổi",       "⇄");
        addNavSection(navContainer, "HỆ THỐNG");
        addNavItem(navContainer, "nguoidung",   "Người dùng",         "◎");

        JScrollPane scroll = new JScrollPane(navContainer);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(8);
        sb.add(scroll, BorderLayout.CENTER);

        // User footer
        sb.add(buildUserFooter(), BorderLayout.SOUTH);
        return sb;
    }

    private JPanel buildLogoPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        p.setBackground(AppTheme.SIDEBAR_BG);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));

        // Icon
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                int s = 5, gap = 2, w = getWidth(), h = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(gap, gap, s, s, 2, 2);
                g2.fillRoundRect(w-s-gap, w-s-gap, s, s, 2, 2);
                g2.setColor(new Color(255,255,255,160));
                g2.fillRoundRect(w-s-gap, gap, s, s, 2, 2);
                g2.fillRoundRect(gap, h-s-gap, s, s, 2, 2);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(28, 28));
        icon.setOpaque(false);
        p.add(icon);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel t1 = new JLabel("TS Manager");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t1.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel t2 = new JLabel("Tuyển sinh 2025");
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        t2.setForeground(AppTheme.TEXT_SECOND);
        textPanel.add(t1);
        textPanel.add(t2);
        p.add(textPanel);
        return p;
    }

    private void addNavSection(JPanel container, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(AppTheme.TEXT_THIRD);
        lbl.setBorder(new EmptyBorder(10, 8, 4, 8));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lbl);
    }

    private void addNavItem(JPanel container, String key, String label, String icon) {
        JPanel item = new JPanel(new BorderLayout()) {
            String panelKey = key;
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (currentPanel.equals(panelKey)) {
                    g2.setColor(AppTheme.SIDEBAR_ACTIVE_BG);
                } else {
                    g2.setColor(getBackground());
                }
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setBackground(AppTheme.SIDEBAR_BG);
        item.setBorder(new EmptyBorder(6, 8, 6, 8));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.putClientProperty("panelKey", key);

        JLabel iconLbl = new JLabel(icon + "  ");
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        iconLbl.setForeground(currentPanel.equals(key) ? AppTheme.SIDEBAR_ACTIVE_TEXT : AppTheme.TEXT_SECOND);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(currentPanel.equals(key)
            ? new Font("Segoe UI", Font.BOLD, 12)
            : new Font("Segoe UI", Font.PLAIN, 12));
        textLbl.setForeground(currentPanel.equals(key) ? AppTheme.SIDEBAR_ACTIVE_TEXT : AppTheme.TEXT_SECOND);

        item.add(iconLbl, BorderLayout.WEST);
        item.add(textLbl, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showPanel(key); }
            @Override public void mouseEntered(MouseEvent e) {
                if (!currentPanel.equals(key)) item.setBackground(AppTheme.BG_SECONDARY);
                item.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                item.setBackground(AppTheme.SIDEBAR_BG);
                item.repaint();
            }
        });

        container.add(item);
        container.add(Box.createVerticalStrut(1));
    }

    private void refreshSidebarHighlight() {
        // Repaint entire sidebar to update active items
        if (sidebarPanel != null) sidebarPanel.repaint();
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel buildUserFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.SIDEBAR_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER),
            new EmptyBorder(8, 10, 8, 10)
        ));

        // Avatar
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String initials = username.length() >= 2 ? username.substring(0,2).toUpperCase() : username.toUpperCase();
                g2.drawString(initials, (getWidth()-fm.stringWidth(initials))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        av.setPreferredSize(new Dimension(30, 30));
        av.setOpaque(false);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(0, 8, 0, 0));
        JLabel name = new JLabel(username);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        name.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel role = new JLabel("Quản trị viên");
        role.setFont(AppTheme.FONT_SMALL);
        role.setForeground(AppTheme.TEXT_SECOND);
        info.add(name);
        info.add(role);

        JButton logout = new JButton("↩");
        logout.setFont(AppTheme.FONT_BODY);
        logout.setForeground(AppTheme.TEXT_SECOND);
        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setToolTipText("Đăng xuất");
        logout.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        p.add(av, BorderLayout.WEST);
        p.add(info, BorderLayout.CENTER);
        p.add(logout, BorderLayout.EAST);
        return p;
    }
}
