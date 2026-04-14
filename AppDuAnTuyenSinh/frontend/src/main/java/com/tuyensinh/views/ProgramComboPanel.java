package com.tuyensinh.views;

import com.tuyensinh.models.ProgramCombination;
import com.tuyensinh.services.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProgramComboPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<ProgramCombination> allData;
    private JTextField[] inputs;

    public ProgramComboPanel() {
        setLayout(new BorderLayout(10, 10));
        allData = DataStore.getInstance().programCombinations;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JLabel title = new JLabel(" Quản lý Ngành - Tổ hợp (xt_nganh_tohop)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // --- Bảng Dữ Liệu ---
        String[] columns = {"ID", "Mã ngành", "Mã Tổ hợp", "M1", "HS1", "M2", "HS2", "M3", "HS3", "Keys", "N1", "TO", "LI", "HO", "SI", "VA", "SU", "DI", "TI", "Khác", "KTPL", "Độ lệch"};
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

        // --- Bảng Detail Master-Detail Bên Phải ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));
        rightPanel.setPreferredSize(new Dimension(450, 0)); // Fixed width for comfortable editing

        JPanel formGrid = new JPanel(new GridLayout(11, 4, 5, 20)); // 11 rows, 4 cols
        inputs = new JTextField[22];
        for(int i=0; i<22; i++) {
            inputs[i] = new JTextField();
            inputs[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        inputs[0].setEditable(false);

        String[] labels = {"ID:", "Mã ngành:", "Mã tổ hợp:", "TB Keys:", "Môn 1:", "HS Môn 1:", "Môn 2:", "HS Môn 2:", "Môn 3:", "HS Môn 3:"};
        for(int i=0; i<10; i++) { formGrid.add(new JLabel(" " + labels[i])); formGrid.add(inputs[i]); }
        String[] keys = {"N1", "TO", "LI", "HO", "SI", "VA", "SU", "DI", "TI", "Khác", "KTPL", "Độ lệch"};
        for(int i=0; i<12; i++) { formGrid.add(new JLabel(" Cờ " + keys[i] + ":")); formGrid.add(inputs[10+i]); }
        
        JScrollPane formScroll = new JScrollPane(formGrid);
        formScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(formScroll, BorderLayout.CENTER);

        // Buttons Grid
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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

        // Bắt sự kiện chọn dòng trên bảng
        table.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromModel(allData.get(table.getSelectedRow()));
            }
        });
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        for (ProgramCombination p : allData) {
            tableModel.addRow(new Object[]{ 
                p.getId(), p.getManganh(), p.getMatohop(), p.getTh_mon1(), p.getHsmon1(), 
                p.getTh_mon2(), p.getHsmon2(), p.getTh_mon3(), p.getHsmon3(), p.getTb_keys(),
                p.getN1(), p.getTo(), p.getLi(), p.getHo(), p.getSi(), p.getVa(), p.getSu(),
                p.getDi(), p.getTi(), p.getKhac(), p.getKtpl(), p.getDolech()
            });
        }
    }

    private void clearForm() {
        for(int i=0; i<22; i++) inputs[i].setText("");
        inputs[0].setText("0"); // Auto-ID hint
        table.clearSelection();
    }

    private void fillFormFromModel(ProgramCombination c) {
        inputs[0].setText(""+c.getId()); inputs[1].setText(c.getManganh()); inputs[2].setText(c.getMatohop()); inputs[3].setText(c.getTb_keys());
        inputs[4].setText(c.getTh_mon1()); inputs[5].setText(""+c.getHsmon1()); inputs[6].setText(c.getTh_mon2()); inputs[7].setText(""+c.getHsmon2());
        inputs[8].setText(c.getTh_mon3()); inputs[9].setText(""+c.getHsmon3());
        inputs[10].setText(""+c.getN1()); inputs[11].setText(""+c.getTo()); inputs[12].setText(""+c.getLi()); inputs[13].setText(""+c.getHo());
        inputs[14].setText(""+c.getSi()); inputs[15].setText(""+c.getVa()); inputs[16].setText(""+c.getSu()); inputs[17].setText(""+c.getDi());
        inputs[18].setText(""+c.getTi()); inputs[19].setText(""+c.getKhac()); inputs[20].setText(""+c.getKtpl()); inputs[21].setText(""+c.getDolech());
    }

    private void extractForm(ProgramCombination c) {
        c.setId(Integer.parseInt(inputs[0].getText().isEmpty() ? "0" : inputs[0].getText()));
        c.setManganh(inputs[1].getText()); c.setMatohop(inputs[2].getText()); c.setTb_keys(inputs[3].getText());
        c.setTh_mon1(inputs[4].getText()); c.setHsmon1(Integer.parseInt(inputs[5].getText().isEmpty() ? "0" : inputs[5].getText()));
        c.setTh_mon2(inputs[6].getText()); c.setHsmon2(Integer.parseInt(inputs[7].getText().isEmpty() ? "0" : inputs[7].getText()));
        c.setTh_mon3(inputs[8].getText()); c.setHsmon3(Integer.parseInt(inputs[9].getText().isEmpty() ? "0" : inputs[9].getText()));
        c.setN1(Integer.parseInt(inputs[10].getText().isEmpty()?"0":inputs[10].getText())); c.setTo(Integer.parseInt(inputs[11].getText().isEmpty()?"0":inputs[11].getText())); 
        c.setLi(Integer.parseInt(inputs[12].getText().isEmpty()?"0":inputs[12].getText())); c.setHo(Integer.parseInt(inputs[13].getText().isEmpty()?"0":inputs[13].getText())); 
        c.setSi(Integer.parseInt(inputs[14].getText().isEmpty()?"0":inputs[14].getText())); c.setVa(Integer.parseInt(inputs[15].getText().isEmpty()?"0":inputs[15].getText()));
        c.setSu(Integer.parseInt(inputs[16].getText().isEmpty()?"0":inputs[16].getText())); c.setDi(Integer.parseInt(inputs[17].getText().isEmpty()?"0":inputs[17].getText())); 
        c.setTi(Integer.parseInt(inputs[18].getText().isEmpty()?"0":inputs[18].getText())); c.setKhac(Integer.parseInt(inputs[19].getText().isEmpty()?"0":inputs[19].getText())); 
        c.setKtpl(Integer.parseInt(inputs[20].getText().isEmpty()?"0":inputs[20].getText())); c.setDolech(Double.parseDouble(inputs[21].getText().isEmpty()?"0":inputs[21].getText()));
    }

    private void doAdd() {
        try {
            ProgramCombination c = new ProgramCombination();
            extractForm(c);
            c.setId(allData.isEmpty() ? 1 : allData.get(allData.size()-1).getId() + 1);
            allData.add(c);
            loadDataToTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!"); }
    }

    private void doEdit() {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa trên bảng!"); return; }
        ProgramCombination c = allData.get(r);
        try {
            extractForm(c);
            loadDataToTable();
            table.setRowSelectionInterval(r, r); // Keep selection
            JOptionPane.showMessageDialog(this, "Lưu thay đổi thành công!");
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!"); }
    }

    private void doDelete() {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa trên bảng!"); return; }
        if(JOptionPane.showConfirmDialog(this, "Chắc chắn Xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            allData.remove(r); loadDataToTable(); clearForm();
        }
    }
}
