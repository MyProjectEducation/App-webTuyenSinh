package ui.panels;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import dao.ThiSinhDAO;
import dao.NganhDAO;
import dao.NguyenVongDAO;
import dao.DiemCongDAO;
import entity.ThiSinh;
import entity.Nganh;

public class DashboardPanel extends BasePanel {

    private JScrollPane scroll;

    public DashboardPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        // Top bar
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnReport = RoundButton.primary("Xuất báo cáo");
        add(buildTopBar("Dashboard", "Tổng quan tuyển sinh 2025", btnImport, btnReport), BorderLayout.NORTH);

        scroll = new JScrollPane();
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.setBackground(AppTheme.BG_TERTIARY);
        add(scroll, BorderLayout.CENTER);

        reloadData();
    }

    public void reloadData() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_TERTIARY);
        content.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Stat cards row
        content.add(buildStatRow());
        content.add(Box.createVerticalStrut(12));

        // Middle row: chart + status
        JPanel midRow = new JPanel(new GridLayout(1, 2, 10, 0));
        midRow.setOpaque(false);
        midRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        midRow.add(buildNganhChart());
        midRow.add(buildStatusPanel());
        content.add(midRow);
        content.add(Box.createVerticalStrut(12));

        // Recent thí sinh
        content.add(buildRecentThiSinh());

        scroll.setViewportView(content);
        scroll.revalidate();
        scroll.repaint();
    }

    private JPanel buildStatRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        DecimalFormat df = new DecimalFormat("#,###");
        
        long tsCount = ThiSinhDAO.countTotalCandidates();
        long nganhCount = new NganhDAO().countTotalNganh();
        long nvCount = new NguyenVongDAO().countTotalNguyenVongs();
        long dcCount = new DiemCongDAO().countTotalDiemCongs();

        row.add(UIComponents.statCard("Thí sinh", df.format(tsCount), AppTheme.PRIMARY, "Họ tên, CCCD, khu vực..."));
        row.add(UIComponents.statCard("Ngành tuyển sinh", df.format(nganhCount), AppTheme.GREEN, "Chỉ tiêu, điểm sàn..."));
        row.add(UIComponents.statCard("Nguyện vọng", df.format(nvCount), AppTheme.AMBER, "Thứ tự, điểm xét tuyển..."));
        row.add(UIComponents.statCard("Điểm cộng", df.format(dcCount), AppTheme.RED, "Tiếng Anh, HSG, tổng"));
        return row;
    }

    private JPanel buildNganhChart() {
        JPanel card = UIComponents.card();
        card.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(9, 12, 9, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
            new EmptyBorder(9, 12, 9, 12)
        ));
        JLabel title = new JLabel("Nguyện vọng theo ngành (Top 8)");
        title.setFont(AppTheme.FONT_BOLD);
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        List<Object[]> top8 = new NguyenVongDAO().getTop8NguyenvongByNganh();
        List<Nganh> nganhs = new NganhDAO().findAll();
        Map<String, String> nganhMap = new HashMap<>();
        for (Nganh n : nganhs) nganhMap.put(n.getMaNganh(), n.getTenNganh());

        long max = 0;
        for (Object[] row : top8) {
            long count = (Long) row[1];
            if (count > max) max = count;
        }
        if (max == 0) max = 1;

        JPanel chartArea = new JPanel();
        chartArea.setLayout(new BoxLayout(chartArea, BoxLayout.Y_AXIS));
        chartArea.setBackground(AppTheme.BG_PRIMARY);
        chartArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        for (Object[] row : top8) {
            String maNganh = (String) row[0];
            String tenNganh = nganhMap.getOrDefault(maNganh, maNganh);
            if (tenNganh == null) tenNganh = "Không xác định";
            
            // Format tenNganh if it's too long
            if (tenNganh.length() > 20) {
                tenNganh = tenNganh.substring(0, 17) + "...";
            }

            long val = (Long) row[1];
            double pct = (double) val / max;

            JPanel barRow = new JPanel(new BorderLayout(6, 0));
            barRow.setOpaque(false);
            barRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            barRow.setBorder(new EmptyBorder(2, 0, 2, 0));

            JLabel lbl = new JLabel(tenNganh);
            lbl.setFont(AppTheme.FONT_SMALL);
            lbl.setForeground(AppTheme.TEXT_SECOND);
            lbl.setPreferredSize(new Dimension(100, 16));

            JPanel barWrap = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(AppTheme.BG_SECONDARY);
                    g2.fillRoundRect(0, 2, getWidth(), getHeight()-4, 4, 4);
                    g2.setColor(AppTheme.PRIMARY);
                    g2.fillRoundRect(0, 2, (int)(getWidth() * pct), getHeight()-4, 4, 4);
                    g2.dispose();
                }
            };
            barWrap.setOpaque(false);

            JLabel valLbl = new JLabel(val >= 1000 ? String.format("%.1fk", val/1000.0) : String.valueOf(val));
            valLbl.setFont(AppTheme.FONT_SMALL);
            valLbl.setForeground(AppTheme.TEXT_PRIMARY);
            valLbl.setPreferredSize(new Dimension(36, 16));
            valLbl.setHorizontalAlignment(SwingConstants.RIGHT);

            barRow.add(lbl, BorderLayout.WEST);
            barRow.add(barWrap, BorderLayout.CENTER);
            barRow.add(valLbl, BorderLayout.EAST);
            chartArea.add(barRow);
        }

        card.add(chartArea, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildStatusPanel() {
        JPanel card = UIComponents.card();
        card.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER),
            new EmptyBorder(9,12,9,12)
        ));
        JLabel title = new JLabel("Trạng thái xét tuyển");
        title.setFont(AppTheme.FONT_BOLD);
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(AppTheme.BG_PRIMARY);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        Map<String, Long> statsMap = new NguyenVongDAO().getStatusStatistics();
        long trúngTuyển = statsMap.getOrDefault("Trúng tuyển", 0L);
        long dướiSàn = statsMap.getOrDefault("Dưới sàn", 0L);
        long chưaXét = statsMap.getOrDefault("Chưa xét", 0L);

        DecimalFormat df = new DecimalFormat("#,###");

        String[][] stats = {
            {"Trúng tuyển", df.format(trúngTuyển), "Đã đạt"},
            {"Dưới sàn",    df.format(dướiSàn),   "Chưa đạt"},
            {"Chưa xét",    df.format(chưaXét),   "Chờ xử lý"},
        };
        Color[] colors = {AppTheme.GREEN, AppTheme.RED, AppTheme.AMBER};

        for (int i = 0; i < stats.length; i++) {
            Color dotColor = colors[i];
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            row.setBorder(new EmptyBorder(6, 0, 6, 0));

            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(dotColor);
                    g2.fillOval(2, 4, 10, 10);
                    g2.dispose();
                }
            };
            dot.setPreferredSize(new Dimension(16, 18));
            dot.setOpaque(false);

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);
            JLabel db = new JLabel(stats[i][0]);
            db.setFont(AppTheme.FONT_MONO);
            db.setForeground(AppTheme.TEXT_SECOND);
            JLabel desc = new JLabel(stats[i][2]);
            desc.setFont(AppTheme.FONT_SMALL);
            desc.setForeground(AppTheme.TEXT_THIRD);
            info.add(db);
            info.add(desc);

            JLabel valLbl = new JLabel(stats[i][1]);
            valLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            valLbl.setForeground(dotColor);

            row.add(dot, BorderLayout.WEST);
            row.add(info, BorderLayout.CENTER);
            row.add(valLbl, BorderLayout.EAST);
            body.add(row);
            if (i < stats.length - 1) body.add(new JSeparator());
        }

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildRecentThiSinh() {
        JPanel card = UIComponents.card();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER),
            new EmptyBorder(9,12,9,12)
        ));
        JLabel title = new JLabel("Thí sinh gần nhất");
        title.setFont(AppTheme.FONT_BOLD);
        header.add(title, BorderLayout.WEST);

        RoundButton viewAll = RoundButton.secondary("Xem tất cả");
        viewAll.addActionListener(e -> mainFrame.showPanel("thisinh"));
        header.add(viewAll, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        String[] cols = {"CCCD", "Họ tên", "Ngày sinh", "Giới tính", "Khu vực", "Đối tượng"};
        
        List<ThiSinh> recentTS = ThiSinhDAO.getTop5RecentCandidates();
        Object[][] data = new Object[recentTS.size()][6];
        
        for (int i = 0; i < recentTS.size(); i++) {
            ThiSinh ts = recentTS.get(i);
            data[i][0] = ts.getCccd();
            data[i][1] = (ts.getHo() != null ? ts.getHo() : "") + " " + (ts.getTen() != null ? ts.getTen() : "");
            data[i][2] = ts.getNgaySinh() != null ? ts.getNgaySinh() : "—";
            data[i][3] = ts.getGioiTinh() != null ? ts.getGioiTinh() : "—";
            data[i][4] = ts.getKhuVuc() != null ? ts.getKhuVuc() : "—";
            data[i][5] = ts.getDoiTuong() != null ? ts.getDoiTuong() : "—";
        }

        JTable table = UIComponents.createTable(cols, data);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }
}
