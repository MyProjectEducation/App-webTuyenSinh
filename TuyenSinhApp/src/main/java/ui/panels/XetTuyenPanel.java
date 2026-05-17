package ui.panels;

import dao.XetTuyenDAO;
import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class XetTuyenPanel extends BasePanel {

    private DefaultTableModel summaryModel;
    private JTable summaryTable;
    private DefaultTableModel detailModel;
    private JTable detailTable;
    private DefaultTableModel methodModel;
    private JTable methodTable;
    
    // Panel chứa thẻ thống kê số liệu trên đỉnh
    private JPanel statRow;

    // Khởi tạo lớp kết nối dữ liệu
    private XetTuyenDAO xtDAO = new XetTuyenDAO();

    private static final String[] SUMMARY_COLUMNS = {
        "Mã ngành", "Tên ngành", "Chỉ tiêu", "Số trúng tuyển",
        "Điểm xét tuyển thấp nhất", "Cao nhất", "Trung bình", "Trạng thái"
    };

    private static final String[] DETAIL_COLUMNS = {
        "Ngành", "CCCD", "Họ tên", "Nguyện vọng",
        "Phương thức", "Tổ hợp", "Điểm xét tuyển", "Kết quả"
    };

    private static final String[] METHOD_COLUMNS = {
        "Mã ngành", "Tên ngành", "THPT", "V-SAT", "ĐGNL", "Tổng"
    };

    public XetTuyenPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
        refreshAllData(); // Đồng bộ dữ liệu thật khi khởi tạo
    }

    private void buildUI() {
        RoundButton btnRun = RoundButton.primary("▶ Chạy xét tuyển");
        RoundButton btnExport = RoundButton.secondary("Xuất kết quả Excel");
        
        btnRun.addActionListener(e -> runXetTuyen());
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Tính năng xuất báo cáo Excel đang được chuẩn bị tích hợp.", "Xuất Excel", JOptionPane.INFORMATION_MESSAGE));

        JPanel topBar = buildTopBar(
            "Kết quả & Thống Kê Xét Tuyển",
            "Dữ liệu tổng hợp thời gian thực từ cơ sở dữ liệu hệ thống",
            btnExport, btnRun
        );

        // Khởi tạo vùng hiển thị thẻ thống kê số liệu
        statRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statRow.setOpaque(false);
        statRow.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Cấu hình bảng Tổng hợp ngành
        summaryModel = new DefaultTableModel(SUMMARY_COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        summaryTable = new JTable(summaryModel);
        UIComponents.styleTable(summaryTable);

        int[] w = {80, 180, 70, 80, 100, 100, 100, 90};
        for (int i = 0; i < w.length && i < summaryTable.getColumnCount(); i++)
            summaryTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // Trình kết xuất màu sắc chỉ tiêu
        summaryTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null && t.getValueAt(row, 2) != null) {
                    try {
                        int chitieu = Integer.parseInt(t.getValueAt(row, 2).toString());
                        int tt = Integer.parseInt(v.toString());
                        setForeground(tt >= chitieu ? AppTheme.GREEN : AppTheme.AMBER);
                        setFont(AppTheme.FONT_BOLD);
                    } catch (Exception e) {}
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        // Trình kết xuất trạng thái đủ/thiếu chỉ tiêu
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

        // Cấu hình bảng Chi tiết trúng tuyển
        detailModel = new DefaultTableModel(DETAIL_COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        detailTable = new JTable(detailModel);
        UIComponents.styleTable(detailTable);

        detailTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = (v != null) ? v.toString() : "";
                if ("Trúng tuyển".equals(val)) { setForeground(AppTheme.GREEN); setText("✓ Trúng tuyển"); }
                else if ("Dưới sàn".equals(val)) { setForeground(AppTheme.AMBER); setText("↓ Dưới sàn"); }
                else { setForeground(AppTheme.TEXT_THIRD); setText("— Chưa xét"); }
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        // Cấu hình bảng Thống kê phương thức
        methodModel = new DefaultTableModel(METHOD_COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        methodTable = new JTable(methodModel);
        UIComponents.styleTable(methodTable);

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

    /**
     * Hàm làm mới và nạp toàn bộ các bảng dữ liệu từ DB lên UI
     */
    public void refreshAllData() {
        loadSummaryData();
        loadDetailData();
        loadMethodData();
        updateStatCards();
    }

    private void loadSummaryData() {
        summaryModel.setRowCount(0);
        List<Object[]> data = xtDAO.getSummaryData();
        if (data != null) {
            for (Object[] row : data) {
                summaryModel.addRow(new Object[]{
                    row[0], row[1], row[2], row[3],
                    String.format("%.2f", Double.parseDouble(row[4].toString())),
                    String.format("%.2f", Double.parseDouble(row[5].toString())),
                    String.format("%.2f", Double.parseDouble(row[6].toString())),
                    row[7]
                });
            }
        }
    }

    private void loadDetailData() {
        detailModel.setRowCount(0);
        List<Object[]> data = xtDAO.getDetailData();
        if (data != null) {
            for (Object[] row : data) {
                detailModel.addRow(new Object[]{
                    row[0], row[1], row[2], row[3], row[4], row[5],
                    String.format("%.2f", Double.parseDouble(row[6].toString())),
                    row[7]
                });
            }
        }
    }

    private void loadMethodData() {
        methodModel.setRowCount(0);
        List<Object[]> data = xtDAO.getMethodData();
        if (data != null) {
            for (Object[] row : data) {
                methodModel.addRow(row);
            }
        }
    }

    /**
     * Cập nhật số lượng động cho 3 ô thống kê trên đỉnh màn hình
     */
    private void updateStatCards() {
        statRow.removeAll(); // Xóa thẻ cũ
        Object[] stats = xtDAO.getGlobalStats();
        
        statRow.add(UIComponents.statCard("Trúng tuyển", String.format("%,d", stats[0]), AppTheme.GREEN, "Đạt chuẩn sàn"));
        statRow.add(UIComponents.statCard("Dưới sàn",    String.format("%,d", stats[1]), AppTheme.AMBER, "Không đạt chuẩn"));
        statRow.add(UIComponents.statCard("Chưa xét",    String.format("%,d", stats[2]), AppTheme.RED,   "Đang chờ xử lý"));
        
        statRow.revalidate();
        statRow.repaint();
    }

    /**
     * Thực thi nút chạy xét tuyển
     */
    private void runXetTuyen() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận kích hoạt hệ thống chạy thuật toán xét tuyển?\\n\\n" +
            "Hệ thống sẽ dựa vào dữ liệu thật dưới database để:\\n" +
            "1. Phân loại điểm thi của thí sinh.\\n" +
            "2. So sánh đối chiếu với mức sàn trần của từng ngành.\\n" +
            "3. Tự động chuyển đổi trạng thái kết quả hàng loạt.",
            "Chạy xét tuyển cục bộ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Chạy mô phỏng tính điểm cập nhật dữ liệu thật dưới DB
            xtDAO.executeAdmissionsSimulation();
            
            // Đổ lại dữ liệu mới nhất lên màn hình
            refreshAllData();
            
            JOptionPane.showMessageDialog(this,
                "Thuật toán chạy hoàn tất! Toàn bộ bảng dữ liệu và thẻ thống kê đã đồng bộ thành công.",
                "Kết quả thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}