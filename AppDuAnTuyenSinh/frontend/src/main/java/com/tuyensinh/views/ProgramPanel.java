package com.tuyensinh.views;

import com.tuyensinh.models.Program;
import com.tuyensinh.services.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProgramPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Program> allData;

    public ProgramPanel() {
        setLayout(new BorderLayout(10, 10));
        allData = DataStore.getInstance().programs;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JLabel title = new JLabel(" Quản lý Danh sách Ngành tuyển sinh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnImport = new JButton("Import");
        
        btnImport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, "Mô phỏng Import Excel Ngành thành công.");
            }
        });
        btnAdd.addActionListener(e -> doAdd());
        btnEdit.addActionListener(e -> doEdit());
        btnDelete.addActionListener(e -> doDelete());

        // Thứ tự chuẩn: Thêm -> Sửa -> Xóa -> Import
        toolbar.add(btnImport);
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);

        String[] columns = {"ID", "Mã ngành", "Tên ngành", "Tổ hợp gốc", "Chỉ tiêu", "Điểm Sàn", "Điểm Trúng tuyển"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30); table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<7; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRender);

        mainContent.add(toolbar, BorderLayout.NORTH);
        mainContent.add(new JScrollPane(table), BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        for (Program c : allData) {
            tableModel.addRow(new Object[]{ 
                c.getIdnganh(), c.getManganh(), c.getTennganh(), c.getnToHopGoc(), c.getnChiTieu(), c.getnDiemSan(), c.getnDiemTrungTuyen() 
            });
        }
    }

    private JPanel createForm(Program p) {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        JTextField[] in = new JTextField[7];
        for(int i=0; i<7; i++) in[i] = new JTextField();
        if(p != null) {
            in[0].setText(""+p.getIdnganh()); in[1].setText(p.getManganh()); in[2].setText(p.getTennganh());
            in[3].setText(p.getnToHopGoc()); in[4].setText(""+p.getnChiTieu());
            in[5].setText(""+p.getnDiemSan()); in[6].setText(""+p.getnDiemTrungTuyen());
        } else { in[0].setText("0"); }
        in[0].setEditable(false);

        String[] cols = {"ID", "Mã ngành", "Tên ngành", "Tổ hợp gốc", "Chỉ tiêu", "Điểm Sàn", "Điểm Trúng tuyển"};
        for(int i=0; i<7; i++) { panel.add(new JLabel(cols[i]+":")); panel.add(in[i]); }
        panel.putClientProperty("in", in); return panel;
    }

    private void extractForm(Program p, JTextField[] in) {
        p.setIdnganh(Integer.parseInt(in[0].getText())); p.setManganh(in[1].getText()); p.setTennganh(in[2].getText());
        p.setnToHopGoc(in[3].getText()); p.setnChiTieu(Integer.parseInt(in[4].getText()));
        p.setnDiemSan(Double.parseDouble(in[5].getText())); p.setnDiemTrungTuyen(Double.parseDouble(in[6].getText()));
    }

    private void doAdd() {
        JPanel panel = createForm(null);
        if(JOptionPane.showConfirmDialog(this, panel, "Thêm Ngành", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Program p = new Program(); extractForm(p, (JTextField[])panel.getClientProperty("in"));
                p.setIdnganh(allData.size() > 0 ? allData.get(allData.size()-1).getIdnganh() + 1 : 1);
                allData.add(p); loadDataToTable();
            } catch(Exception ex) {}
        }
    }

    private void doEdit() {
        int r = table.getSelectedRow(); if(r==-1) return;
        Program p = allData.get(r); JPanel panel = createForm(p);
        if(JOptionPane.showConfirmDialog(this, panel, "Sửa Ngành", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try { extractForm(p, (JTextField[])panel.getClientProperty("in")); loadDataToTable(); } catch(Exception ex){}
        }
    }

    private void doDelete() {
        int r = table.getSelectedRow(); if(r==-1) return;
        allData.remove(r); loadDataToTable();
    }
}
