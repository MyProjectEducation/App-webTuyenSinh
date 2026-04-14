package com.tuyensinh.views;

import com.tuyensinh.models.Combination;
import com.tuyensinh.services.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CombinationPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Combination> allCombos;

    public CombinationPanel() {
        setLayout(new BorderLayout(10, 10));
        allCombos = DataStore.getInstance().combinations;

        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JLabel title = new JLabel(" Quản lý Tổ hợp môn xét tuyển");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnImport = new JButton("Import Excel");

        btnAdd.addActionListener(e -> doAdd());
        btnEdit.addActionListener(e -> doEdit());
        btnDelete.addActionListener(e -> doDelete());

        toolbar.add(btnImport);
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);

        // Bảng dữ liệu theo DB schema
        String[] columns = {"ID", "Mã tổ hợp", "Môn 1", "Môn 2", "Môn 3", "Tên tổ hợp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }
        // Resize ID column
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(table);

        mainContent.add(toolbar, BorderLayout.NORTH);
        mainContent.add(scroll, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        for (Combination c : allCombos) {
            tableModel.addRow(new Object[]{ 
                c.getIdtohop(), c.getMatohop(), c.getMon1(), c.getMon2(), c.getMon3(), c.getTentohop() 
            });
        }
    }

    private void doAdd() {
        JTextField txtId = new JTextField();
        JTextField txtMa = new JTextField();
        JTextField txtMon1 = new JTextField();
        JTextField txtMon2 = new JTextField();
        JTextField txtMon3 = new JTextField();
        JTextField txtTen = new JTextField();

        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.add(new JLabel("ID Tổ hợp:")); p.add(txtId);
        p.add(new JLabel("Mã tổ hợp:")); p.add(txtMa);
        p.add(new JLabel("Mã môn 1:")); p.add(txtMon1);
        p.add(new JLabel("Mã môn 2:")); p.add(txtMon2);
        p.add(new JLabel("Mã môn 3:")); p.add(txtMon3);
        p.add(new JLabel("Tên tổ hợp (Diễn giải):")); p.add(txtTen);
        
        int option = JOptionPane.showConfirmDialog(this, p, "Thêm Tổ Hợp (Dữ liệu mẫu)", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION) {
            try {
                Combination c = new Combination(
                    Integer.parseInt(txtId.getText()), txtMa.getText(), txtMon1.getText(),
                    txtMon2.getText(), txtMon3.getText(), txtTen.getText()
                );
                allCombos.add(c);
                loadDataToTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Nhập liệu sai định dạng!");
            }
        }
    }

    private void doEdit() {
        int selected = table.getSelectedRow();
        if(selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tổ hợp!"); return;
        }
        Combination c = allCombos.get(selected);

        JTextField txtId = new JTextField(String.valueOf(c.getIdtohop()));
        JTextField txtMa = new JTextField(c.getMatohop());
        JTextField txtMon1 = new JTextField(c.getMon1());
        JTextField txtMon2 = new JTextField(c.getMon2());
        JTextField txtMon3 = new JTextField(c.getMon3());
        JTextField txtTen = new JTextField(c.getTentohop());

        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.add(new JLabel("ID Tổ hợp:")); p.add(txtId);
        p.add(new JLabel("Mã tổ hợp:")); p.add(txtMa);
        p.add(new JLabel("Mã môn 1:")); p.add(txtMon1);
        p.add(new JLabel("Mã môn 2:")); p.add(txtMon2);
        p.add(new JLabel("Mã môn 3:")); p.add(txtMon3);
        p.add(new JLabel("Tên tổ hợp:")); p.add(txtTen);
        
        int option = JOptionPane.showConfirmDialog(this, p, "Sửa Tổ Hợp", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION) {
            c.setIdtohop(Integer.parseInt(txtId.getText()));
            c.setMatohop(txtMa.getText());
            c.setMon1(txtMon1.getText());
            c.setMon2(txtMon2.getText());
            c.setMon3(txtMon3.getText());
            c.setTentohop(txtTen.getText());
            loadDataToTable();
        }
    }

    private void doDelete() {
        int[] selectedRows = table.getSelectedRows();
        if(selectedRows.length == 0) { return; }
        
        int ans = JOptionPane.showConfirmDialog(this, "Xóa các tổ hợp đang chọn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if(ans == JOptionPane.YES_OPTION) {
            for(int i = selectedRows.length -1; i >= 0; i--) {
                allCombos.remove(selectedRows[i]);
            }
            loadDataToTable();
        }
    }
}
