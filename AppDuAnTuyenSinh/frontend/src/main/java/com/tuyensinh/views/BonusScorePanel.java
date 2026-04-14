package com.tuyensinh.views;

import com.tuyensinh.models.BonusPoint;
import com.tuyensinh.services.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BonusScorePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<BonusPoint> allData;
    private JTextField[] inputs;

    public BonusScorePanel() {
        setLayout(new BorderLayout(10, 10));
        allData = DataStore.getInstance().bonusPoints;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JLabel title = new JLabel(" Quản lý Điểm Cộng (xt_diemcongxettuyen)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Table
        String[] columns = {"ID", "CCCD Thí sinh", "Mã Ngành", "Tổ Hợp", "Phương thức", "Điểm CC", "Điểm UTXT", "Tổng", "Ghi chú", "DC Keys"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30); table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRender);

        mainContent.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Master Detail Form Bên Phải ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Nhập thông tin"));
        rightPanel.setPreferredSize(new Dimension(400, 0));

        JPanel formGrid = new JPanel(new GridLayout(10, 2, 10, 10));
        inputs = new JTextField[10];
        for(int i=0; i<10; i++) { inputs[i] = new JTextField(); inputs[i].setFont(new Font("Segoe UI", Font.PLAIN, 14)); }
        inputs[0].setEditable(false);

        String[] cols = {"ID:", "CCCD:", "Mã Ngành:", "Tổ Hợp:", "P.Thức:", "Điểm CC:", "Điểm UTXT:", "Tổng:", "Ghi chú:", "DC Keys:"};
        for(int i=0; i<10; i++) { formGrid.add(new JLabel(" " + cols[i])); formGrid.add(inputs[i]); }
        
        rightPanel.add(new JScrollPane(formGrid), BorderLayout.CENTER);

        JPanel topTool = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnImport = new JButton("Import Excel");
        btnImport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Giả lập Import..."));
        topTool.add(btnImport);
        add(topTool, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnClear = new JButton("Reset");
        JButton btnAdd = new JButton("Thêm mới");
        JButton btnEdit = new JButton("Lưu thay đổi");
        JButton btnDelete = new JButton("Xóa");
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> doAdd());
        btnEdit.addActionListener(e -> doEdit());
        btnDelete.addActionListener(e -> doDelete());

        btnPanel.add(btnClear); btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        mainContent.add(rightPanel, BorderLayout.EAST);
        add(mainContent, BorderLayout.CENTER);

        // Events
        table.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromModel(allData.get(table.getSelectedRow()));
            }
        });
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        for (BonusPoint b : allData) {
            tableModel.addRow(new Object[]{ 
                b.getIddiemcong(), b.getTs_cccd(), b.getManganh(), b.getMatohop(), b.getPhuongthuc(),
                b.getDiemCC(), b.getDiemUtxt(), b.getDiemTong(), b.getGhichu(), b.getDc_keys()
            });
        }
    }

    private void clearForm() {
        for(int i=0; i<10; i++) inputs[i].setText("");
        inputs[0].setText("0"); table.clearSelection();
    }

    private void fillFormFromModel(BonusPoint b) {
        inputs[0].setText(""+b.getIddiemcong()); inputs[1].setText(b.getTs_cccd()); inputs[2].setText(b.getManganh()); inputs[3].setText(b.getMatohop());
        inputs[4].setText(b.getPhuongthuc()); inputs[5].setText(""+b.getDiemCC()); inputs[6].setText(""+b.getDiemUtxt()); inputs[7].setText(""+b.getDiemTong());
        inputs[8].setText(b.getGhichu()); inputs[9].setText(b.getDc_keys());
    }

    private void extractForm(BonusPoint b) {
        b.setIddiemcong(Integer.parseInt(inputs[0].getText().isEmpty() ? "0" : inputs[0].getText()));
        b.setTs_cccd(inputs[1].getText()); b.setManganh(inputs[2].getText()); b.setMatohop(inputs[3].getText());
        b.setPhuongthuc(inputs[4].getText()); b.setDiemCC(Double.parseDouble(inputs[5].getText().isEmpty()?"0":inputs[5].getText())); 
        b.setDiemUtxt(Double.parseDouble(inputs[6].getText().isEmpty()?"0":inputs[6].getText()));
        b.setDiemTong(Double.parseDouble(inputs[7].getText().isEmpty()?"0":inputs[7].getText())); 
        b.setGhichu(inputs[8].getText()); b.setDc_keys(inputs[9].getText());
    }

    private void doAdd() {
        try {
            BonusPoint b = new BonusPoint(); extractForm(b);
            b.setIddiemcong(allData.isEmpty() ? 1 : allData.get(allData.size()-1).getIddiemcong() + 1);
            allData.add(b); loadDataToTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Đã thêm điểm cộng!");
        } catch(Exception ex) {}
    }

    private void doEdit() {
        int r = table.getSelectedRow(); if(r==-1) { JOptionPane.showMessageDialog(this, "Chọn dòng để sửa"); return; }
        BonusPoint b = allData.get(r);
        try { extractForm(b); loadDataToTable(); table.setRowSelectionInterval(r, r); } catch(Exception ex){}
    }

    private void doDelete() {
        int r = table.getSelectedRow(); if(r==-1) { JOptionPane.showMessageDialog(this, "Chọn dòng để xóa"); return; }
        allData.remove(r); loadDataToTable(); clearForm();
    }
}
