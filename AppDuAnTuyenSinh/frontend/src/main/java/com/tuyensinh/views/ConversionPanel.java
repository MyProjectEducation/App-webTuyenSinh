package com.tuyensinh.views;

import com.tuyensinh.models.Conversion;
import com.tuyensinh.services.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ConversionPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Conversion> allData;
    private List<Conversion> filteredData;
    private JTextField[] inputs;
    private JTextField txtSearch;

    public ConversionPanel() {
        setLayout(new BorderLayout(10, 10));
        allData = DataStore.getInstance().conversions;
        filteredData = allData;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JLabel title = new JLabel(" Quản lý Bảng Quy Đổi (xt_bangquydoi)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Table List Configuration
        String[] columns = {"ID", "Phương thức", "Tổ Hợp", "Môn", "Điểm A", "Điểm B", "Điểm C", "Điểm D", "Mã quy đổi", "Phân vị"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30); table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRender);

        // Center Body containing Search + Table
        JPanel centerBody = new JPanel(new BorderLayout());
        JPanel searchTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập phương thức, mã quy đổi...");
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> doSearch());
        searchTool.add(txtSearch); searchTool.add(btnSearch);

        centerBody.add(searchTool, BorderLayout.NORTH);
        centerBody.add(new JScrollPane(table), BorderLayout.CENTER);
        mainContent.add(centerBody, BorderLayout.CENTER);

        // --- Detail Form Bên Phải ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Nhập thông tin"));
        rightPanel.setPreferredSize(new Dimension(350, 0));

        JPanel formGrid = new JPanel(new GridLayout(10, 2, 10, 10));
        inputs = new JTextField[10];
        for(int i=0; i<10; i++) { inputs[i] = new JTextField(); inputs[i].setFont(new Font("Segoe UI", Font.PLAIN, 14)); }
        inputs[0].setEditable(false);

        String[] cols = {"ID:", "PThức:", "T.Hợp:", "Môn:", "Điểm A:", "Điểm B:", "Điểm C:", "Điểm D:", "Mã quy đổi:", "Phân vị:"};
        for(int i=0; i<10; i++) { formGrid.add(new JLabel(" " + cols[i])); formGrid.add(inputs[i]); }
        
        rightPanel.add(new JScrollPane(formGrid), BorderLayout.CENTER);

        JPanel topTool = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnQd1 = new JButton("Quy đổi ĐGNL");
        JButton btnQd2 = new JButton("Quy đổi VSAT");
        JButton btnImport = new JButton("Import EXCEL");
        topTool.add(btnQd1); topTool.add(btnQd2); topTool.add(btnImport);
        rightPanel.add(topTool, BorderLayout.NORTH);

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

        table.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromModel(filteredData.get(table.getSelectedRow()));
            }
        });
    }

    private void doSearch() {
        String query = txtSearch.getText().trim().toLowerCase();
        if(query.isEmpty()) {
            filteredData = allData;
        } else {
            filteredData = allData.stream().filter(c -> 
                (c.getD_phuongthuc() != null && c.getD_phuongthuc().toLowerCase().contains(query)) ||
                (c.getD_maquydoi() != null && c.getD_maquydoi().toLowerCase().contains(query))
            ).collect(Collectors.toList());
        }
        loadDataToTable();
        clearForm();
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        for (Conversion c : filteredData) {
            tableModel.addRow(new Object[]{ 
                c.getIdqd(), c.getD_phuongthuc(), c.getD_tohop(), c.getD_mon(),
                c.getD_diema(), c.getD_diemb(), c.getD_diemc(), c.getD_diemd(),
                c.getD_maquydoi(), c.getD_phanvi()
            });
        }
    }

    private void clearForm() {
        for(int i=0; i<10; i++) inputs[i].setText("");
        inputs[0].setText("0"); table.clearSelection();
    }

    private void fillFormFromModel(Conversion c) {
        inputs[0].setText(""+c.getIdqd()); inputs[1].setText(c.getD_phuongthuc()); inputs[2].setText(c.getD_tohop()); inputs[3].setText(c.getD_mon());
        inputs[4].setText(""+c.getD_diema()); inputs[5].setText(""+c.getD_diemb()); inputs[6].setText(""+c.getD_diemc()); inputs[7].setText(""+c.getD_diemd());
        inputs[8].setText(c.getD_maquydoi()); inputs[9].setText(c.getD_phanvi());
    }

    private void extractForm(Conversion c) {
        c.setIdqd(Integer.parseInt(inputs[0].getText().isEmpty() ? "0" : inputs[0].getText()));
        c.setD_phuongthuc(inputs[1].getText()); c.setD_tohop(inputs[2].getText()); c.setD_mon(inputs[3].getText());
        c.setD_diema(Double.parseDouble(inputs[4].getText().isEmpty()?"0":inputs[4].getText())); c.setD_diemb(Double.parseDouble(inputs[5].getText().isEmpty()?"0":inputs[5].getText())); 
        c.setD_diemc(Double.parseDouble(inputs[6].getText().isEmpty()?"0":inputs[6].getText())); c.setD_diemd(Double.parseDouble(inputs[7].getText().isEmpty()?"0":inputs[7].getText())); 
        c.setD_maquydoi(inputs[8].getText()); c.setD_phanvi(inputs[9].getText());
    }

    private void doAdd() {
        try {
            Conversion c = new Conversion(); extractForm(c);
            c.setIdqd(allData.isEmpty() ? 1 : allData.get(allData.size()-1).getIdqd() + 1);
            allData.add(c); doSearch(); clearForm();
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu số!");}
    }

    private void doEdit() {
        int r = table.getSelectedRow(); if(r==-1) return;
        Conversion c = filteredData.get(r);
        try { extractForm(c); loadDataToTable(); table.setRowSelectionInterval(r, r); } catch(Exception ex){}
    }

    private void doDelete() {
        int r = table.getSelectedRow(); if(r==-1) return;
        allData.remove(filteredData.get(r)); doSearch(); clearForm();
    }
}
