package com.tuyensinh.views;

import com.tuyensinh.models.Aspiration;
import com.tuyensinh.services.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AspirationPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Aspiration> allData;
    private JTextField[] inputs;

    public AspirationPanel() {
        setLayout(new BorderLayout(10, 10));
        allData = DataStore.getInstance().aspirations;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JLabel title = new JLabel(" Quản lý Nguyện vọng & Xét tuyển (xt_nguyenvongxettuyen)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Table
        String[] columns = {"ID", "CCCD", "Mã Ngành", "Thứ tự NV", "Điểm THXT", "Điểm ƯTQĐ", "Điểm Cộng", "Điểm Xét", "Kết quả", "NV Keys", "PThức", "T.Hợp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30); table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRender);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainContent.add(scroll, BorderLayout.CENTER);

        // --- Detail Form Bên Phải ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Thông tin NV"));
        rightPanel.setPreferredSize(new Dimension(400, 0));

        JPanel formGrid = new JPanel(new GridLayout(12, 2, 10, 10));
        inputs = new JTextField[12];
        for(int i=0; i<12; i++) { inputs[i] = new JTextField(); inputs[i].setFont(new Font("Segoe UI", Font.PLAIN, 14)); }
        inputs[0].setEditable(false);

        String[] cols = {"ID:", "CCCD:", "Mã Ngành:", "Thứ tự NV:", "Điểm THXT:", "Điểm ƯTQĐ:", "Điểm Cộng:", "Điểm Xét:", "Kết quả:", "NV Keys:", "PThức:", "T.Hợp:"};
        for(int i=0; i<12; i++) { formGrid.add(new JLabel(" " + cols[i])); formGrid.add(inputs[i]); }
        
        rightPanel.add(new JScrollPane(formGrid), BorderLayout.CENTER);

        JPanel topTool = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnProcess = new JButton("Thực hiện Xét tuyển (Mô phỏng)");
        btnProcess.addActionListener(e -> doEvaluate());
        topTool.add(btnProcess);
        add(topTool, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnClear = new JButton("Reset Form");
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
                fillFormFromModel(allData.get(table.getSelectedRow()));
            }
        });
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        for (Aspiration a : allData) {
            tableModel.addRow(new Object[]{ 
                a.getIdnv(), a.getNn_cccd(), a.getNv_manganh(), a.getNv_tt(), a.getDiem_thxt(),
                a.getDiem_utqd(), a.getDiem_cong(), a.getDiem_xettuyen(), a.getNv_ketqua(),
                a.getNv_keys(), a.getTt_phuongthuc(), a.getTt_thm()
            });
        }
    }

    private void clearForm() {
        for(int i=0; i<12; i++) inputs[i].setText("");
        inputs[0].setText("0"); table.clearSelection();
    }

    private void fillFormFromModel(Aspiration a) {
        inputs[0].setText(""+a.getIdnv()); inputs[1].setText(a.getNn_cccd()); inputs[2].setText(a.getNv_manganh()); inputs[3].setText(""+a.getNv_tt());
        inputs[4].setText(""+a.getDiem_thxt()); inputs[5].setText(""+a.getDiem_utqd()); inputs[6].setText(""+a.getDiem_cong()); inputs[7].setText(""+a.getDiem_xettuyen());
        inputs[8].setText(a.getNv_ketqua()); inputs[9].setText(a.getNv_keys()); inputs[10].setText(a.getTt_phuongthuc()); inputs[11].setText(a.getTt_thm());
    }

    private void extractForm(Aspiration a) {
        a.setIdnv(Integer.parseInt(inputs[0].getText().isEmpty() ? "0" : inputs[0].getText()));
        a.setNn_cccd(inputs[1].getText()); a.setNv_manganh(inputs[2].getText()); a.setNv_tt(Integer.parseInt(inputs[3].getText().isEmpty()?"0":inputs[3].getText()));
        a.setDiem_thxt(Double.parseDouble(inputs[4].getText().isEmpty()?"0":inputs[4].getText())); a.setDiem_utqd(Double.parseDouble(inputs[5].getText().isEmpty()?"0":inputs[5].getText())); 
        a.setDiem_cong(Double.parseDouble(inputs[6].getText().isEmpty()?"0":inputs[6].getText())); a.setDiem_xettuyen(Double.parseDouble(inputs[7].getText().isEmpty()?"0":inputs[7].getText())); 
        a.setNv_ketqua(inputs[8].getText()); a.setNv_keys(inputs[9].getText());
        a.setTt_phuongthuc(inputs[10].getText()); a.setTt_thm(inputs[11].getText());
    }

    private void doEvaluate() {
        JOptionPane.showMessageDialog(this, "Tiến hành mô phỏng giải thuật xét tuyển.\nTất cả NV có điểm xét > 20.0 sẽ đổ thành 'Trúng tuyển'");
        for(Aspiration a : allData) {
            if(a.getDiem_xettuyen() > 20.0) a.setNv_ketqua("Trúng tuyển");
            else a.setNv_ketqua("Rớt");
        }
        loadDataToTable();
    }

    private void doAdd() {
        try {
            Aspiration a = new Aspiration(); extractForm(a);
            a.setIdnv(allData.isEmpty() ? 1 : allData.get(allData.size()-1).getIdnv() + 1);
            allData.add(a); loadDataToTable(); clearForm();
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!"); }
    }

    private void doEdit() {
        int r = table.getSelectedRow(); if(r==-1) { JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa!"); return; }
        Aspiration a = allData.get(r);
        try { extractForm(a); loadDataToTable(); table.setRowSelectionInterval(r, r); } catch(Exception ex){}
    }

    private void doDelete() {
        int r = table.getSelectedRow(); if(r==-1) return;
        allData.remove(r); loadDataToTable(); clearForm();
    }
}
