package com.tuyensinh.views;

import com.tuyensinh.DAO.CandidateDAO;
import com.tuyensinh.models.Candidate;
import com.tuyensinh.services.DataStore;
import com.tuyensinh.services.ReadFile;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class CandidatePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblPagination;
    private JTextField[] inputs;

    private List<Candidate> allCandidates;
    private List<Candidate> filteredCandidates;
    private int currentPage = 1;
    private final int PAGE_SIZE = 20;

    public CandidatePanel() {
        setLayout(new BorderLayout(10, 10));
        allCandidates = DataStore.getInstance().candidates;
        filteredCandidates = allCandidates;
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JLabel title = new JLabel(" Quản lý Thí sinh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));

        // Bảng dữ liệu
        String[] columns = {
                "ID", "CCCD", "SBD", "Họ", "Tên", "Ngày Sinh", "Giới Tính",
                "Điện thoại", "Email", "Nơi sinh", "Khu vực", "Đối tượng"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(150);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // --- Master Detail Form Bên Phải ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Hồ sơ Cá nhân"));
        rightPanel.setPreferredSize(new Dimension(380, 0));

        JPanel formGrid = new JPanel(new GridLayout(13, 2, 5, 10));
        inputs = new JTextField[13];
        for (int i = 0; i < 13; i++) {
            inputs[i] = new JTextField();
            inputs[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        inputs[0].setEditable(false); // ID

        String[] cols = { "ID:", "CCCD:", "SBD:", "Họ lót:", "Tên:", "Ngày Sinh:", "Điện thoại:", "Password:",
                "Giới tính:", "Email:", "Nơi sinh:", "Đối tượng:", "Khu vực:" };
        for (int i = 0; i < 13; i++) {
            formGrid.add(new JLabel(" " + cols[i]));
            formGrid.add(inputs[i]);
        }

        JScrollPane flexScroll = new JScrollPane(formGrid);
        flexScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(flexScroll, BorderLayout.CENTER);

        // Nút Toolbar Trên Detail
        JPanel topDetail = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnImport = new JButton("Import Excel");
        btnImport.addActionListener(e -> doImport());
        topDetail.add(btnImport);
        rightPanel.add(topDetail, BorderLayout.NORTH);

        // Nút Thao tác dưới Detail
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnClear = new JButton("Reset Form");
        JButton btnAdd = new JButton("Thêm mới");
        JButton btnEdit = new JButton("Lưu thay đổi");
        JButton btnDelete = new JButton("Xóa");
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> doAdd());
        btnEdit.addActionListener(e -> doEdit());
        btnDelete.addActionListener(e -> doDelete());

        btnPanel.add(btnClear);
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        // Khu vực Tìm kiếm và Phân trang (Center layout body)
        JPanel centerBody = new JPanel(new BorderLayout());
        JPanel searchTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> doSearch());
        searchTool.add(txtSearch);
        searchTool.add(btnSearch);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnPrev = new JButton("< Trang trước");
        JButton btnNext = new JButton("Trang sau >");
        lblPagination = new JLabel("Trang 1/1");
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadDataToTable();
                table.clearSelection();
                clearForm();
            }
        });
        btnNext.addActionListener(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                loadDataToTable();
                table.clearSelection();
                clearForm();
            }
        });
        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPagination);
        paginationPanel.add(btnNext);

        centerBody.add(searchTool, BorderLayout.NORTH);
        centerBody.add(scroll, BorderLayout.CENTER);
        centerBody.add(paginationPanel, BorderLayout.SOUTH);

        mainContent.add(rightPanel, BorderLayout.EAST);
        mainContent.add(centerBody, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int dataIdx = (currentPage - 1) * PAGE_SIZE + table.getSelectedRow();
                fillFormFromModel(filteredCandidates.get(dataIdx));
            }
        });
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) filteredCandidates.size() / PAGE_SIZE);
    }

    private void doSearch() {
        String kw = txtSearch.getText().toLowerCase();
        filteredCandidates = allCandidates.stream()
                .filter(c -> c.getCccd().toLowerCase().equals(kw) ||
                        (c.getHo() + " " + c.getTen()).toLowerCase().contains(kw))
                .collect(Collectors.toList());
        currentPage = 1;
        loadDataToTable();
        clearForm();
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        int totalItems = filteredCandidates.size();
        int totalPages = getTotalPages();
        if (totalPages == 0)
            totalPages = 1;
        lblPagination.setText("Trang " + currentPage + " / " + totalPages);

        int startIdx = (currentPage - 1) * PAGE_SIZE;
        int endIdx = Math.min(startIdx + PAGE_SIZE, totalItems);

        for (int i = startIdx; i < endIdx; i++) {
            Candidate c = filteredCandidates.get(i);
            tableModel.addRow(new Object[] {
                    c.getIdthisinh(), c.getCccd(), c.getSobaodanh(), c.getHo(), c.getTen(),
                    c.getNgaySinh(), c.getGioiTinh(), c.getDienThoai(), c.getEmail(),
                    c.getNoiSinh(), c.getKhuVuc(), c.getDoiTuong()
            });
        }
    }

    private void clearForm() {
        for (int i = 0; i < 13; i++)
            inputs[i].setText("");
        inputs[0].setText("0");
        table.clearSelection();
    }

    private void fillFormFromModel(Candidate c) {
        inputs[0].setText(String.valueOf(c.getIdthisinh()));
        inputs[1].setText(c.getCccd());
        inputs[2].setText(c.getSobaodanh());
        inputs[3].setText(c.getHo());
        inputs[4].setText(c.getTen());
        inputs[5].setText(c.getNgaySinh());
        inputs[6].setText(c.getDienThoai());
        inputs[7].setText(c.getPassword());
        inputs[8].setText(c.getGioiTinh());
        inputs[9].setText(c.getEmail());
        inputs[10].setText(c.getNoiSinh());
        inputs[11].setText(c.getDoiTuong());
        inputs[12].setText(c.getKhuVuc());
    }

    private void extractForm(Candidate a) {
        a.setIdthisinh(Integer.parseInt(inputs[0].getText().isEmpty() ? "0" : inputs[0].getText()));
        a.setCccd(inputs[1].getText());
        a.setSobaodanh(inputs[2].getText());
        a.setHo(inputs[3].getText());
        a.setTen(inputs[4].getText());
        a.setNgaySinh(inputs[5].getText());
        a.setDienThoai(inputs[6].getText());
        a.setPassword(inputs[7].getText());
        a.setGioiTinh(inputs[8].getText());
        a.setEmail(inputs[9].getText());
        a.setNoiSinh(inputs[10].getText());
        a.setDoiTuong(inputs[11].getText());
        a.setKhuVuc(inputs[12].getText());
        a.setUpdatedAt(LocalDate.now().toString());
    }

    private void doAdd() {
        try {
            validateForm();
            if (CandidateDAO.getCandidateByCCCD(inputs[1].getText()) != null) {
                throw new Exception("CCCD đã tồn tại trong hệ thống!");
            }
            Candidate c = new Candidate();
            extractForm(c);
            c.setIdthisinh(
                    allCandidates.size() > 0 ? allCandidates.get(allCandidates.size() - 1).getIdthisinh() + 1 : 1);
            allCandidates.add(c);
            CandidateDAO.createCandidate(c);
            doSearch();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + e.getMessage());
        }
    }

    private void doEdit() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Chọn thí sinh!");
            return;
        }
        int dataIdx = (currentPage - 1) * PAGE_SIZE + r;
        Candidate target = filteredCandidates.get(dataIdx);
        try {
            validateForm();
            extractForm(target);
            CandidateDAO.updateCandidate(target);
            loadDataToTable();
            table.setRowSelectionInterval(r, r);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage());
        }
    }

    private void doDelete() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0)
            return;
        if (JOptionPane.showConfirmDialog(this, "Xóa " + rows.length + " thí sinh?", "Xóa",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = rows.length - 1; i >= 0; i--) {
                int dataIdx = (currentPage - 1) * PAGE_SIZE + rows[i];
                Candidate candidate = filteredCandidates.get(dataIdx);
                allCandidates.remove(candidate);
                CandidateDAO.deleteCandidate(candidate);
            }
            doSearch();
        }
    }

    private void validateForm() throws Exception {
        // 1. Kiểm tra trống cho tất cả các trường
        if (inputs[1].getText().trim().isEmpty())
            throw new Exception("CCCD không được để trống!");
        if (inputs[2].getText().trim().isEmpty())
            throw new Exception("Số báo danh không được để trống!");
        if (inputs[3].getText().trim().isEmpty())
            throw new Exception("Họ lót không được để trống!");
        if (inputs[4].getText().trim().isEmpty())
            throw new Exception("Tên không được để trống!");
        if (inputs[5].getText().trim().isEmpty())
            throw new Exception("Ngày sinh không được để trống!");
        if (inputs[6].getText().trim().isEmpty())
            throw new Exception("Điện thoại không được để trống!");
        if (inputs[7].getText().trim().isEmpty())
            throw new Exception("Password không được để trống!");
        if (inputs[8].getText().trim().isEmpty())
            throw new Exception("Giới tính không được để trống!");
        if (inputs[9].getText().trim().isEmpty())
            throw new Exception("Email không được để trống!");
        if (inputs[10].getText().trim().isEmpty())
            throw new Exception("Nơi sinh không được để trống!");
        if (inputs[11].getText().trim().isEmpty())
            throw new Exception("Đối tượng không được để trống!");
        if (inputs[12].getText().trim().isEmpty())
            throw new Exception("Khu vực không được để trống!");

        // 2. Kiểm tra định dạng chuyên sâu (Nâng cao)

        // Kiểm tra độ dài CCCD (thường là 12 số)
        String cccd = inputs[1].getText().trim();
        if (!cccd.matches("\\d{12}")) {
            throw new Exception("CCCD phải bao gồm 12 chữ số!");
        }

        // Kiểm tra định dạng Email
        String email = inputs[9].getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Định dạng Email không hợp lệ!");
        }

        // Kiểm tra Số điện thoại (9-11 chữ số)
        String phone = inputs[6].getText().trim();
        if (!phone.matches("\\d{9,11}")) {
            throw new Exception("Số điện thoại phải từ 9 đến 11 chữ số!");
        }

        // Kiểm tra Password (ví dụ tối thiểu 6 ký tự)
        if (inputs[7].getText().length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự!");
        }

        // Kiểm tra CCCD đã tồn tại (chỉ khi thêm mới, không kiểm tra khi đang sửa chính
        // thí sinh đó)

    }

    private void doImport() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            System.out.println("Selected file: " + fileChooser.getSelectedFile().getAbsolutePath());
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            ReadFile.readFileCandidate(filePath);
        }
        doSearch();
        clearForm();
    }
}
