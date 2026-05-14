package ui.panels;

import dao.BangQuyDoiDAO;
import entity.BangQuyDoi;
import ui.MainFrame;
import ui.components.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class BangQuyDoiPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboPhuongThuc;
    
    private BangQuyDoiDAO bqdDAO = new BangQuyDoiDAO();

    private static final String[] COLUMNS = {
        "ID", "Phương thức", "Tổ hợp", "Môn",
        "Điểm A", "Điểm B", "Điểm C", "Điểm D",
        "Mã quy đổi", "Phân vị", "Hành động"
    };

    public BangQuyDoiPanel(MainFrame mainFrame) {
        super(mainFrame);
        initComponents();
        loadData(); 
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(AppTheme.BG_PRIMARY);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- Header Section ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Bảng Quy Đổi Điểm");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        
        cboPhuongThuc = new JComboBox<>(new String[]{"Tất cả phương thức", "DGNL", "DGTD", "THPT"});
        cboPhuongThuc.setPreferredSize(new Dimension(180, 35));

        actions.add(new JLabel("Tìm kiếm:"));
        actions.add(txtSearch);
        actions.add(cboPhuongThuc);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        // --- Table Section ---
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Chỉ cho phép cột cuối cùng (Hành động) có thể click/sửa
                return col == COLUMNS.length - 1; 
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35); // Tăng chiều cao dòng để vừa vặn nút bấm
        
        // Thiết lập cấu hình hiển thị tiêu đề bảng cơ bản
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        // Đổ bộ dựng giao diện nút bấm (Renderer & Editor) vào cột Hành động
        int actionColumnIndex = COLUMNS.length - 1;
        table.getColumnModel().getColumn(actionColumnIndex).setCellRenderer(new ActionPanelRenderer());
        table.getColumnModel().getColumn(actionColumnIndex).setCellEditor(new ActionPanelEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Hàm nạp dữ liệu thật bằng Hibernate
     */
    public void loadData() {
        try {
            tableModel.setRowCount(0);
            List<BangQuyDoi> list = bqdDAO.getAll();
            if (list != null) {
                for (BangQuyDoi bqd : list) {
                    tableModel.addRow(new Object[]{
                        bqd.getIdqd(),
                        bqd.getPhuongThuc(),
                        bqd.getToHop(),
                        bqd.getMon(),
                        bqd.getDiemA(),
                        bqd.getDiemB(),
                        bqd.getDiemC(),
                        bqd.getDiemD(),
                        bqd.getMaQuyDoi(),
                        bqd.getPhanVi(),
                        "" // Ô trống để Renderer tự vẽ nút bấm lên
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi nạp dữ liệu vào BangQuyDoiPanel: " + e.getMessage());
        }
    }

    /**
     * Hàm xử lý Logic khi người dùng nhấn nút Sửa
     */
    public void executeSửa(int idqd) {
        JOptionPane.showMessageDialog(this, "Hệ thống chuẩn bị mở Form chỉnh sửa cho bản ghi ID: " + idqd, "Chức năng Sửa", JOptionPane.INFORMATION_MESSAGE);
        // Bạn có thể viết code gọi JDialog hoặc Form sửa tại đây
    }

    /**
     * Hàm xử lý Logic khi người dùng nhấn nút Xóa
     */
    public void executeXóa(int idqd, int rowIndex) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa dòng quy đổi có ID: " + idqd + " không?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Thực hiện xóa dòng trên giao diện JTable trước mắt
            tableModel.removeRow(rowIndex);
            JOptionPane.showMessageDialog(this, "Đã xóa bản ghi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            // Sau này bạn chỉ cần gọi thêm lệnh: bqdDAO.delete(idqd); để đồng bộ xóa mất dưới DB thật.
        }
    }

    // ==========================================
    // INNER CLASSES: BỘ DỰNG GIAO DIỆN NÚT BẤM
    // ==========================================

    class ActionPanelRenderer extends JPanel implements TableCellRenderer {
        private JButton btnSửa = new JButton("Sửa");
        private JButton btnXóa = new JButton("Xóa");

        public ActionPanelRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setOpaque(true);
            
            // Định dạng màu sắc dựa theo AppTheme của hệ thống
            btnSửa.setBackground(AppTheme.BG_SECONDARY);
            btnSửa.setForeground(AppTheme.TEXT_PRIMARY);
            btnSửa.setFocusPainted(false);
            
            btnXóa.setBackground(AppTheme.RED_LIGHT);
            btnXóa.setForeground(AppTheme.RED);
            btnXóa.setFocusPainted(false);
            
            add(btnSửa);
            add(btnXóa);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : AppTheme.BG_PRIMARY);
            }
            return this;
        }
    }

    class ActionPanelEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        private JButton btnSửa = new JButton("Sửa");
        private JButton btnXóa = new JButton("Xóa");
        private int currentRow;

        public ActionPanelEditor(JCheckBox checkBox) {
            super(checkBox);
            panel.setOpaque(true);
            
            btnSửa.setBackground(AppTheme.BG_SECONDARY);
            btnSửa.setForeground(AppTheme.TEXT_PRIMARY);
            btnXóa.setBackground(AppTheme.RED_LIGHT);
            btnXóa.setForeground(AppTheme.RED);

            // Bắt sự kiện khi click nút Sửa
            btnSửa.addActionListener(e -> {
                fireEditingStopped(); // Ngừng trạng thái edit ô để tránh lỗi giao diện
                int idqd = (int) table.getValueAt(currentRow, 0); // Lấy ID thực của bản ghi ở cột 0
                executeSửa(idqd);
            });

            // Bắt sự kiện khi click nút Xóa
            btnXóa.addActionListener(e -> {
                fireEditingStopped();
                int idqd = (int) table.getValueAt(currentRow, 0);
                executeXóa(idqd, currentRow);
            });

            panel.add(btnSửa);
            panel.add(btnXóa);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row; // Ghi nhận dòng hiện tại đang tương tác
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}