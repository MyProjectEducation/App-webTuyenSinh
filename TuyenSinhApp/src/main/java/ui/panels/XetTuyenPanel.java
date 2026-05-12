package ui.panels;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class XetTuyenPanel extends BasePanel {

    private DefaultTableModel summaryModel;
    private JTable summaryTable;
    private DefaultTableModel detailModel;
    private JTable detailTable;
    private DefaultTableModel methodModel;
    private JTable methodTable;

    private static final String[] SUMMARY_COLUMNS = {
        "Mã ngành", "Tên ngành", "Chỉ tiêu", "Số trúng tuyển",
        "Điểm xét tuyển thấp nhất", "Cao nhất", "Trung bình", "Trạng thái"
    };

    private static final Object[][] SUMMARY_DATA = {
        {"7140202", "Giáo dục Tiểu học", 200, 200, 18.50, 29.50, 21.50, "yes"},
        {"7140201", "Giáo dục Mầm non",  200, 200, 17.25, 28.00, 20.30, "yes"},
        {"7140231", "Sư phạm Tiếng Anh", 120, 118, 22.00, 30.00, 26.50, "yes"},
        {"7140209", "Sư phạm Toán học",  40,  35,  24.50, 30.00, 28.00, "duoisan"},
        {"7140217", "Sư phạm Ngữ văn",   50,  50,  21.00, 29.50, 25.50, "yes"},
        {"7140114", "Quản lý giáo dục",  40,  40,  17.00, 27.00, 21.00, "yes"},
        {"7140221", "Sư phạm Âm nhạc",   75,  60,  18.00, 26.00, 22.00, "duoisan"},
    };

    private static final String[] DETAIL_COLUMNS = {
        "Ngành", "CCCD", "Họ tên", "Nguyện vọng",
        "Phương thức", "Tổ hợp", "Điểm xét tuyển", "Kết quả"
    };

    private static final Object[][] DETAIL_DATA = {
        {"Sư phạm Tiếng Anh", "001207008830", "Hoàng Văn Em", 2, "PT2", "D01", 21.18, "Trúng tuyển"},
        {"Giáo dục Tiểu học", "001207012341", "Bùi Văn Hùng", 1, "PT2", "A01", 19.93, "Trúng tuyển"},
        {"Sư phạm Toán học",  "001207006913", "Lê Văn Cường", 4, "PT2", "A00", 20.25, "Chưa xét"},
    };

    private static final String[] METHOD_COLUMNS = {
        "Mã ngành", "Tên ngành", "THPT", "V-SAT", "ĐGNL", "Tổng"
    };

    private static final Object[][] METHOD_DATA = {
        {"7140231", "Sư phạm Tiếng Anh", 80, 25, 13, 118},
        {"7140202", "Giáo dục Tiểu học", 120, 60, 20, 200},
        {"7140201", "Giáo dục Mầm non",  150, 35, 15, 200},
    };

    public XetTuyenPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
    }

    private void buildUI() {
        RoundButton btnRun = RoundButton.primary("▶ Chạy xét tuyển");
        RoundButton btnExport = RoundButton.secondary("Xuất kết quả Excel");
        btnRun.addActionListener(e -> runXetTuyen());
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "(Demo) Sẽ xuất kết quả ra file Excel.", "Xuất Excel", JOptionPane.INFORMATION_MESSAGE));

        JPanel topBar = buildTopBar(
            "Kết quả xét tuyển",
            "Chỉ làm nền UI, chưa kết nối dữ liệu thật",
            btnExport, btnRun
        );

        // Stat row
        JPanel statRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statRow.setOpaque(false);
        statRow.setBorder(new EmptyBorder(12, 14, 12, 14));
        statRow.add(UIComponents.statCard("Trúng tuyển", "1,284", AppTheme.GREEN, "Đã đạt"));
        statRow.add(UIComponents.statCard("Dưới sàn",    "716",   AppTheme.AMBER, "Chưa đạt"));
        statRow.add(UIComponents.statCard("Chưa xét",    "412",   AppTheme.RED,   "Chờ xử lý"));

        summaryModel = new DefaultTableModel(SUMMARY_COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        summaryTable = new JTable(summaryModel);
        UIComponents.styleTable(summaryTable);
        loadSummaryData();

        int[] w = {80, 180, 70, 80, 100, 100, 100, 90};
        for (int i = 0; i < w.length && i < summaryTable.getColumnCount(); i++)
            summaryTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // so_trungtd vs chitieu renderer
        summaryTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null) {
                    int chitieu = Integer.parseInt(t.getValueAt(row, 2).toString());
                    int tt = Integer.parseInt(v.toString());
                    setForeground(tt >= chitieu ? AppTheme.GREEN : AppTheme.AMBER);
                    setFont(AppTheme.FONT_BOLD);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        // trang_thai
        summaryTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("yes".equals(v)) { setForeground(AppTheme.GREEN); setText("✓ Đủ chỉ tiêu"); }
                else { setForeground(AppTheme.AMBER); setText("↓ Thiếu chỉ tiêu"); }
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        detailModel = new DefaultTableModel(DETAIL_COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        detailTable = new JTable(detailModel);
        UIComponents.styleTable(detailTable);
        loadDetailData();

        detailTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("Trúng tuyển".equals(v)) { setForeground(AppTheme.GREEN); setText("✓ Trúng tuyển"); }
                else if ("Dưới sàn".equals(v)) { setForeground(AppTheme.AMBER); setText("↓ Dưới sàn"); }
                else { setForeground(AppTheme.TEXT_THIRD); setText("— Chưa xét"); }
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        methodModel = new DefaultTableModel(METHOD_COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        methodTable = new JTable(methodModel);
        UIComponents.styleTable(methodTable);
        loadMethodData();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY);
        tabs.addTab("Tổng hợp ngành", new JScrollPane(summaryTable));
        tabs.addTab("Chi tiết trúng tuyển", new JScrollPane(detailTable));
        tabs.addTab("Theo phương thức", new JScrollPane(methodTable));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(topBar, BorderLayout.NORTH);
        topSection.add(statRow, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(topSection, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void loadSummaryData() {
        summaryModel.setRowCount(0);
        for (Object[] row : SUMMARY_DATA) summaryModel.addRow(row);
    }

    private void loadDetailData() {
        detailModel.setRowCount(0);
        for (Object[] row : DETAIL_DATA) detailModel.addRow(row);
    }

    private void loadMethodData() {
        methodModel.setRowCount(0);
        for (Object[] row : METHOD_DATA) methodModel.addRow(row);
    }

    private void runXetTuyen() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận chạy xét tuyển?\n\n" +
            "Hệ thống sẽ:\n" +
            "1. Tính điểm xét tuyển\n" +
            "2. So sánh với điểm sàn của từng ngành\n" +
            "3. Cập nhật kết quả: Trúng tuyển / Dưới sàn\n" +
            "4. Sắp xếp theo thứ tự nguyện vọng",
            "Chạy xét tuyển", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "(Demo) Hoàn thành! Đã cập nhật kết quả xét tuyển.",
                "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            loadSummaryData();
            loadDetailData();
            loadMethodData();
        }
    }
}
