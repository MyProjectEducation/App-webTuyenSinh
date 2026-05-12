package ui.panels;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {

    protected MainFrame mainFrame;

    public BasePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_TERTIARY);
    }

    // Build a standard top bar
    protected JPanel buildTopBar(String title, String breadcrumb, JComponent... rightComponents) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppTheme.BG_PRIMARY);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
            new EmptyBorder(10, 16, 10, 16)
        ));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(AppTheme.FONT_TITLE);
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel bcLbl = new JLabel(breadcrumb);
        bcLbl.setFont(AppTheme.FONT_SMALL);
        bcLbl.setForeground(AppTheme.TEXT_SECOND);

        left.add(titleLbl);
        left.add(bcLbl);

        bar.add(left, BorderLayout.WEST);

        if (rightComponents.length > 0) {
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            right.setOpaque(false);
            for (JComponent c : rightComponents) right.add(c);
            bar.add(right, BorderLayout.EAST);
        }

        return bar;
    }

    // Build search/filter bar
    protected JPanel buildSearchBar(JComponent... components) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(AppTheme.BG_SECONDARY);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));
        for (JComponent c : components) bar.add(c);
        return bar;
    }

    // Wrap table in scroll
    protected JScrollPane wrapTable(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(AppTheme.BG_PRIMARY);
        return sp;
    }

    // Pagination bar
    protected JPanel buildPagination(int currentPage, int totalPages, int totalRows, Runnable[] pageActions) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));
        bar.setBackground(AppTheme.BG_SECONDARY);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));

        for (int i = 1; i <= Math.min(totalPages, 7); i++) {
            final int page = i;
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(AppTheme.FONT_SMALL);
            btn.setPreferredSize(new Dimension(26, 26));
            btn.setFocusPainted(false);
            btn.setBorderPainted(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (i == currentPage) {
                btn.setBackground(AppTheme.PRIMARY);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY));
            } else {
                btn.setBackground(AppTheme.BG_PRIMARY);
                btn.setForeground(AppTheme.TEXT_SECOND);
                btn.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
            }
            if (pageActions != null && page <= pageActions.length) {
                btn.addActionListener(e -> pageActions[page - 1].run());
            }
            bar.add(btn);
        }

        JLabel info = new JLabel("  Tổng: " + totalRows + " bản ghi  |  " + totalPages + " trang");
        info.setFont(AppTheme.FONT_SMALL);
        info.setForeground(AppTheme.TEXT_SECOND);
        bar.add(info);

        return bar;
    }
}
