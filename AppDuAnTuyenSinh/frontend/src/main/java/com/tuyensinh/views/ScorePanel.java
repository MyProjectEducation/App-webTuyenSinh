package com.tuyensinh.views;

import com.tuyensinh.models.CandidateScore;
import com.tuyensinh.services.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScorePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<CandidateScore> allData;
    private JTextField[] inputs;

    public ScorePanel() {
        setLayout(new BorderLayout(10, 10));
        allData = DataStore.getInstance().scores;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JLabel title = new JLabel(" Quản lý Điểm Thí Sinh (ĐGNL, THPT, VSAT)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Bảng Dữ liệu
        String[] columns = {"ID", "CCCD", "SBD", "P.Thức", "TO", "LI", "HO", "SI", "SU", "DI", "VA", "N1_THI", "N1_CC", "CNCN", "CNNN", "TI", "KTPL", "NL1", "NK1", "NK2"};
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
        rightPanel.setBorder(BorderFactory.createTitledBorder("Nhập điểm Thí sinh"));
        rightPanel.setPreferredSize(new Dimension(500, 0));

        JPanel formGrid = new JPanel(new GridLayout(10, 4, 10, 20));
        inputs = new JTextField[20];
        for(int i=0; i<20; i++) {
            inputs[i] = new JTextField();
            inputs[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        inputs[0].setEditable(false);

        String[] cols = {"ID:", "CCCD:", "SBD:", "P.Thức:", "TO:", "LI:", "HO:", "SI:", "SU:", "DI:", "VA:", "N1_THI:", "N1_CC:", "CNCN:", "CNNN:", "TI:", "KTPL:", "NL1:", "NK1:", "NK2:"};
        for(int i=0; i<20; i++) { formGrid.add(new JLabel(" " + cols[i])); formGrid.add(inputs[i]); }
        
        JScrollPane formScroll = new JScrollPane(formGrid);
        formScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(formScroll, BorderLayout.CENTER);

        // Nút thao tác
        JPanel topTool = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnChart = new JButton("Thống kê");
        JButton btnImport = new JButton("Import EXCEL");
        btnChart.addActionListener(e -> doChart());
        btnImport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Mô phỏng Import..."));
        topTool.add(btnImport); topTool.add(btnChart);
        add(topTool, BorderLayout.NORTH); // Header actions

        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnClear = new JButton("Reset Forms");
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

        // Bắt sự kiện Click Table
        table.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromModel(allData.get(table.getSelectedRow()));
            }
        });
    }

    private void doChart() {
        long ptCount = allData.stream().filter(c -> c.getD_phuongthuc().toUpperCase().contains("PT")).count();
        long dgnlCount = allData.stream().filter(c -> c.getD_phuongthuc().toUpperCase().contains("DGNL") || c.getD_phuongthuc().toUpperCase().contains("ĐGNL")).count();
        long vsatCount = allData.stream().filter(c -> c.getD_phuongthuc().toUpperCase().contains("VSAT")).count();
        
        double avgToan = allData.stream().filter(c -> c.getTo() > 0).mapToDouble(CandidateScore::getTo).average().orElse(0);
        double avgLy = allData.stream().filter(c -> c.getLi() > 0).mapToDouble(CandidateScore::getLi).average().orElse(0);
        double avgHoa = allData.stream().filter(c -> c.getHo() > 0).mapToDouble(CandidateScore::getHo).average().orElse(0);
        double avgVan = allData.stream().filter(c -> c.getVa() > 0).mapToDouble(CandidateScore::getVa).average().orElse(0);

        String msg = "--- THỐNG KÊ CHI TIẾT ĐIỂM THÍ SINH ---\n\n" +
                     "[1] Theo Loại điểm (Phương thức):\n" +
                     "   - Xét tuyển THPT: " + ptCount + " hồ sơ\n" +
                     "   - Xét tuyển ĐGNL: " + dgnlCount + " hồ sơ\n" +
                     "   - Xét tuyển VSAT: " + vsatCount + " hồ sơ\n" +
                     "   - Khác: " + (allData.size() - ptCount - dgnlCount - vsatCount) + " hồ sơ\n\n" +
                     "[2] Điểm trung bình môn (Lọc các mốc > 0):\n" +
                     "   - Môn Toán: " + String.format("%.2f", avgToan) + " điểm\n" +
                     "   - Môn Lý: " + String.format("%.2f", avgLy) + " điểm\n" +
                     "   - Môn Hóa: " + String.format("%.2f", avgHoa) + " điểm\n" +
                     "   - Môn Văn: " + String.format("%.2f", avgVan) + " điểm";

        JOptionPane.showMessageDialog(this, msg, "Báo cáo Thống kê Điểm Cụ thể", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        for (CandidateScore p : allData) {
            tableModel.addRow(new Object[]{ 
                p.getIddiemthi(), p.getCccd(), p.getSobaodanh(), p.getD_phuongthuc(),
                p.getTo(), p.getLi(), p.getHo(), p.getSi(), p.getSu(), p.getDi(), p.getVa(),
                p.getN1_thi(), p.getN1_cc(), p.getCncn(), p.getCnnn(), p.getTi(), p.getKtpl(),
                p.getNl1(), p.getNk1(), p.getNk2()
            });
        }
    }

    private void clearForm() {
        for(int i=0; i<20; i++) inputs[i].setText("");
        inputs[0].setText("0"); table.clearSelection();
    }

    private void fillFormFromModel(CandidateScore c) {
        inputs[0].setText(""+c.getIddiemthi()); inputs[1].setText(c.getCccd()); inputs[2].setText(c.getSobaodanh()); inputs[3].setText(c.getD_phuongthuc());
        inputs[4].setText(""+c.getTo()); inputs[5].setText(""+c.getLi()); inputs[6].setText(""+c.getHo()); inputs[7].setText(""+c.getSi());
        inputs[8].setText(""+c.getSu()); inputs[9].setText(""+c.getDi()); inputs[10].setText(""+c.getVa()); inputs[11].setText(""+c.getN1_thi());
        inputs[12].setText(""+c.getN1_cc()); inputs[13].setText(""+c.getCncn()); inputs[14].setText(""+c.getCnnn()); inputs[15].setText(""+c.getTi());
        inputs[16].setText(""+c.getKtpl()); inputs[17].setText(""+c.getNl1()); inputs[18].setText(""+c.getNk1()); inputs[19].setText(""+c.getNk2());
    }

    private void extractForm(CandidateScore c) {
        c.setIddiemthi(Integer.parseInt(inputs[0].getText().isEmpty() ? "0" : inputs[0].getText())); 
        c.setCccd(inputs[1].getText()); c.setSobaodanh(inputs[2].getText()); c.setD_phuongthuc(inputs[3].getText());
        c.setTo(Double.parseDouble(inputs[4].getText().isEmpty()?"0":inputs[4].getText())); c.setLi(Double.parseDouble(inputs[5].getText().isEmpty()?"0":inputs[5].getText())); 
        c.setHo(Double.parseDouble(inputs[6].getText().isEmpty()?"0":inputs[6].getText())); c.setSi(Double.parseDouble(inputs[7].getText().isEmpty()?"0":inputs[7].getText())); 
        c.setSu(Double.parseDouble(inputs[8].getText().isEmpty()?"0":inputs[8].getText())); c.setDi(Double.parseDouble(inputs[9].getText().isEmpty()?"0":inputs[9].getText()));
        c.setVa(Double.parseDouble(inputs[10].getText().isEmpty()?"0":inputs[10].getText())); c.setN1_thi(Double.parseDouble(inputs[11].getText().isEmpty()?"0":inputs[11].getText())); 
        c.setN1_cc(Double.parseDouble(inputs[12].getText().isEmpty()?"0":inputs[12].getText())); c.setCncn(Double.parseDouble(inputs[13].getText().isEmpty()?"0":inputs[13].getText())); 
        c.setCnnn(Double.parseDouble(inputs[14].getText().isEmpty()?"0":inputs[14].getText())); c.setTi(Double.parseDouble(inputs[15].getText().isEmpty()?"0":inputs[15].getText()));
        c.setKtpl(Double.parseDouble(inputs[16].getText().isEmpty()?"0":inputs[16].getText())); c.setNl1(Double.parseDouble(inputs[17].getText().isEmpty()?"0":inputs[17].getText())); 
        c.setNk1(Double.parseDouble(inputs[18].getText().isEmpty()?"0":inputs[18].getText())); c.setNk2(Double.parseDouble(inputs[19].getText().isEmpty()?"0":inputs[19].getText()));
    }

    private void doAdd() {
        try {
            CandidateScore c = new CandidateScore(); extractForm(c);
            c.setIddiemthi(allData.isEmpty() ? 1 : allData.get(allData.size()-1).getIddiemthi() + 1);
            allData.add(c); loadDataToTable(); clearForm();
            JOptionPane.showMessageDialog(this, "Đã thêm điểm thí sinh!");
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi cú pháp!"); }
    }

    private void doEdit() {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa!"); return; }
        CandidateScore c = allData.get(r);
        try {
            extractForm(c); loadDataToTable(); table.setRowSelectionInterval(r, r);
            JOptionPane.showMessageDialog(this, "Đã lưu thay đổi!");
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi cú pháp!"); }
    }

    private void doDelete() {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!"); return; }
        if(JOptionPane.showConfirmDialog(this, "Chắc xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            allData.remove(r); loadDataToTable(); clearForm();
        }
    }
}
