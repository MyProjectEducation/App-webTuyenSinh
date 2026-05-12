package ui.components;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class UIComponents {

    // ─── Rounded Button ───────────────────────────────────────────────────────
    public static class RoundButton extends JButton {
        private Color bg, hoverBg, fg;
        private boolean hovered = false;

        public RoundButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            this.hoverBg = bg.darker();
            this.fg = fg;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(AppTheme.FONT_BODY);
            setForeground(fg);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(6, 14, 6, 14));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        public static RoundButton primary(String text) {
            return new RoundButton(text, AppTheme.PRIMARY, Color.WHITE);
        }

        public static RoundButton secondary(String text) {
            RoundButton btn = new RoundButton(text, AppTheme.BG_SECONDARY, AppTheme.TEXT_PRIMARY);
            btn.setHoverBg(AppTheme.BORDER);
            return btn;
        }

        public static RoundButton danger(String text) {
            return new RoundButton(text, AppTheme.RED_LIGHT, AppTheme.RED);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hovered ? hoverBg : bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }

        public void setHoverBg(Color hoverBg) {
            this.hoverBg = hoverBg;
        }
    }

    // ─── Badge Label ──────────────────────────────────────────────────────────
    public static class Badge extends JLabel {
        public Badge(String text, Color bg, Color fg) {
            super(text);
            setOpaque(false);
            setFont(AppTheme.FONT_SMALL);
            setForeground(fg);
            setBorder(new EmptyBorder(2, 8, 2, 8));
            setBackground(bg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }

        public static Badge blue(String text)  { return new Badge(text, AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY); }
        public static Badge green(String text) { return new Badge(text, AppTheme.GREEN_LIGHT, AppTheme.GREEN); }
        public static Badge amber(String text) { return new Badge(text, AppTheme.AMBER_LIGHT, AppTheme.AMBER); }
        public static Badge red(String text)   { return new Badge(text, AppTheme.RED_LIGHT, AppTheme.RED); }
        public static Badge gray(String text)  { return new Badge(text, AppTheme.BG_SECONDARY, AppTheme.TEXT_SECOND); }
        public static Badge teal(String text)  { return new Badge(text, AppTheme.TEAL_LIGHT, AppTheme.TEAL); }
    }

    // ─── Styled Table ─────────────────────────────────────────────────────────
    public static JTable createTable(String[] columns, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);
        return table;
    }

    public static JTable createTable(String[] columns) {
        return createTable(columns, new Object[0][0]);
    }

    public static void styleTable(JTable table) {
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(AppTheme.ROW_HEIGHT);
        table.setGridColor(AppTheme.BORDER);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(AppTheme.PRIMARY_LIGHT);
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);
        table.setBackground(AppTheme.BG_PRIMARY);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(AppTheme.FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(AppTheme.BG_SECONDARY);
        table.getTableHeader().setForeground(AppTheme.TEXT_SECOND);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));
        table.getTableHeader().setReorderingAllowed(false);
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                    boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    // ─── Scrollable Table Panel ───────────────────────────────────────────────
    public static JScrollPane scrollTable(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        sp.getViewport().setBackground(AppTheme.BG_PRIMARY);
        return sp;
    }

    // ─── Section Panel (card) ─────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(AppTheme.BG_PRIMARY);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        return p;
    }

    // ─── Search Field ────────────────────────────────────────────────────────
    public static JTextField searchField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(AppTheme.TEXT_THIRD);
                    g2.setFont(getFont());
                    g2.drawString(placeholder, 8, getHeight() / 2 + 5);
                    g2.dispose();
                }
            }
        };
        tf.setFont(AppTheme.FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            new EmptyBorder(4, 8, 4, 8)
        ));
        tf.setBackground(AppTheme.BG_PRIMARY);
        return tf;
    }

    // ─── Combo Box ────────────────────────────────────────────────────────────
    public static JComboBox<String> comboBox(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(AppTheme.FONT_BODY);
        cb.setBackground(AppTheme.BG_PRIMARY);
        return cb;
    }

    // ─── Form Field ──────────────────────────────────────────────────────────
    public static JTextField formField(String value) {
        JTextField tf = new JTextField(value);
        tf.setFont(AppTheme.FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            new EmptyBorder(5, 9, 5, 9)
        ));
        return tf;
    }

    public static JTextField formField() { return formField(""); }

    // ─── Form Label ──────────────────────────────────────────────────────────
    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECOND);
        return lbl;
    }

    // ─── Separator ───────────────────────────────────────────────────────────
    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER);
        return sep;
    }

    // ─── Stat Card ────────────────────────────────────────────────────────────
    public static JPanel statCard(String label, String value, Color valueColor, String sub) {
        JPanel card = new JPanel(new BorderLayout(0, 3));
        card.setBackground(AppTheme.BG_PRIMARY);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECOND);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(valueColor);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(AppTheme.FONT_SMALL);
        subLbl.setForeground(AppTheme.TEXT_THIRD);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lbl, BorderLayout.NORTH);
        top.add(val, BorderLayout.CENTER);
        top.add(subLbl, BorderLayout.SOUTH);

        card.add(top, BorderLayout.CENTER);
        return card;
    }
}
