package ui.panels;

import dao.NguyenVongDAO;
import dao.ThiSinhDAO;
import dao.DiemCongDAO;
import dao.NganhDAO;
import dao.NganhTohopDAO;
import dao.TohopMonDAO;
import dao.DiemThiSinhDAO;
import entity.NguyenVong;
import entity.ThiSinh;
import entity.DiemCong;
import entity.Nganh;
import entity.NganhTohop;
import entity.TohopMon;
import entity.DiemThiSinh;

import ui.MainFrame;
import ui.components.AppTheme;
import ui.components.UIComponents;
import ui.components.UIComponents.RoundButton;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class NguyenVongPanel extends BasePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboKetqua;

    private NguyenVongDAO nvDAO = new NguyenVongDAO();
    private List<Object[]> dbData = new ArrayList<>();

    private static final String[] COLUMNS = {
        "ID", "CCCD", "Họ tên", "Nguyện vọng", "Ngành",
        "Tổ hợp", "Điểm tổ hợp", "Điểm cộng",
        "Điểm ưu tiên", "Điểm xét tuyển", "Phương thức",
        "Kết quả", "Hành động"
    };

    private static final int COL_ID = 0;
    private static final int COL_CCCD = 1;
    private static final int COL_HOTEN = 2;
    private static final int COL_NV = 3;
    private static final int COL_NGANH = 4;
    private static final int COL_TOHOP = 5;
    private static final int COL_DIEM_TOHOP = 6;
    private static final int COL_DIEM_CONG = 7;
    private static final int COL_DIEM_UT = 8;
    private static final int COL_DIEM_XT = 9;
    private static final int COL_PT = 10;
    private static final int COL_KETQUA = 11;
    private static final int COL_ACTIONS = 12;

    public NguyenVongPanel(MainFrame mainFrame) {
        super(mainFrame);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        RoundButton btnImport = RoundButton.secondary("Import Excel");
        RoundButton btnAdd    = RoundButton.primary("+ Thêm NV");
        btnImport.addActionListener(e -> showImport());
        btnAdd.addActionListener(e -> showDialog(-1));

        cboKetqua = UIComponents.comboBox("Tất cả kết quả", "Trúng tuyển", "Dưới sàn", "Chưa xét");
        cboKetqua.addActionListener(e -> filterData());

        JPanel topBar = buildTopBar(
            "Điểm xét tuyển theo nguyện vọng",
            "ĐGNL/V-SAT được quy đổi thang 30 trước khi tính điểm xét tuyển",
            cboKetqua, btnImport, btnAdd
        );

        txtSearch = UIComponents.searchField("Tìm CCCD, họ tên, ngành...");
        txtSearch.setPreferredSize(new Dimension(250, 30));
        
        // Cải tiến: Thiết lập tìm kiếm thực tế trên bảng
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch(txtSearch.getText().trim());
            }
        });

        RoundButton btnS = RoundButton.secondary("Tìm");
        btnS.addActionListener(e -> performSearch(txtSearch.getText().trim()));
        JPanel searchBar = buildSearchBar(new JLabel("  Tìm: "), txtSearch, btnS);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == COL_ACTIONS;
            }
        };
        table = new JTable(tableModel);
        UIComponents.styleTable(table);

        int[] w = {50, 110, 140, 80, 85, 90, 85, 85, 85, 95, 90, 95, 110};
        for (int i = 0; i < w.length && i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // Định dạng hiển thị điểm số an toàn (tránh NullPointerException)
        DefaultTableCellRenderer scoreR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null && !v.toString().trim().isEmpty()) {
                    try {
                        setText(String.format("%.4f", Double.parseDouble(v.toString())));
                    } catch (Exception ex) { setText(v.toString()); }
                } else {
                    setText("0.0000");
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        };
        for (int i : new int[]{COL_DIEM_TOHOP, COL_DIEM_CONG, COL_DIEM_UT}) 
            table.getColumnModel().getColumn(i).setCellRenderer(scoreR);

        // Cột điểm xét tuyển nổi bật thu hút ánh nhìn
        table.getColumnModel().getColumn(COL_DIEM_XT).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null && !v.toString().trim().isEmpty()) {
                    try {
                        setText(String.format("%.4f", Double.parseDouble(v.toString())));
                    } catch (Exception ex) { setText(v.toString()); }
                    setForeground(AppTheme.PRIMARY); 
                    setFont(AppTheme.FONT_BOLD);
                } else {
                    setText("0.0000");
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        // Vẽ trạng thái Badge trúng tuyển/dưới sàn
        table.getColumnModel().getColumn(COL_KETQUA).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = (v != null) ? v.toString().trim() : "";
                if ("Trúng tuyển".equals(val)) { setForeground(AppTheme.GREEN); setText("✓ Trúng tuyển"); }
                else if ("Dưới sàn".equals(val)) { setForeground(AppTheme.AMBER); setText("↓ Dưới sàn"); }
                else { setForeground(AppTheme.TEXT_THIRD); setText("— Chưa xét"); }
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY);
                return this;
            }
        });

        table.getColumn("Hành động").setCellRenderer(new ActionPanelRenderer());
        table.getColumn("Hành động").setCellEditor(new ActionPanelEditor());
        table.getColumnModel().getColumn(COL_PT).setCellRenderer(new PhuongThucRenderer());

        JPanel topSec = new JPanel(new BorderLayout());
        topSec.add(topBar, BorderLayout.NORTH);
        topSec.add(searchBar, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(topSec, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void performSearch(String text) {
        if (text.isEmpty()) {
            filterData();
            return;
        }
        String q = text.toLowerCase();
        List<Object[]> filtered = new ArrayList<>();
        for (Object[] row : dbData) {
            String cccd = row[0] != null ? row[0].toString().toLowerCase() : "";
            String hoTen = row[1] != null ? row[1].toString().toLowerCase() : "";
            String manganh = row[3] != null ? row[3].toString().toLowerCase() : "";
            if (cccd.contains(q) || hoTen.contains(q) || manganh.contains(q)) {
                filtered.add(row);
            }
        }
        loadData(filtered);
    }

    public void refreshData() {
        List<Object[]> list = nvDAO.getAllForPanel();
        dbData.clear();
        if (list != null) {
            dbData.addAll(list);
        }
        filterData(); 
    }

    private void loadData(List<Object[]> data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            Object[] r = new Object[COLUMNS.length];
            r[COL_ID] = row[11];
            r[COL_CCCD] = row[0];
            r[COL_HOTEN] = row[1];
            r[COL_NV] = row[2];
            r[COL_NGANH] = row[3];
            r[COL_TOHOP] = row[4];
            r[COL_DIEM_TOHOP] = row[5];
            r[COL_DIEM_CONG] = row[6];
            r[COL_DIEM_UT] = row[7];
            r[COL_DIEM_XT] = row[8];
            r[COL_PT] = row[9];
            r[COL_KETQUA] = row[10];
            r[COL_ACTIONS] = "";
            tableModel.addRow(r);
        }
    }

    private void filterData() {
        String sel = cboKetqua.getSelectedItem().toString();
        if (sel.startsWith("Tất cả")) { loadData(dbData); return; }
        
        List<Object[]> filtered = new ArrayList<>();
        for (Object[] row : dbData) {
            String status = (row[10] != null) ? row[10].toString().trim() : "";
            if (status.isEmpty()) status = "Chưa xét";
            
            if (sel.equals(status)) {
                filtered.add(row);
            }
        }
        loadData(filtered);
    }

    private void confirmDelete(int row) {
        int idnv = (Integer) tableModel.getValueAt(row, COL_ID);
        int c = JOptionPane.showConfirmDialog(this,
            "Xóa nguyện vọng ID: " + idnv + " của thí sinh " + tableModel.getValueAt(row, COL_HOTEN) + "?",
            "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            try {
                nvDAO.deleteById(idnv);
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa dữ liệu: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateToHopOptions(String maNganh, JComboBox<String> cboToHop, String selectedToHop) {
        cboToHop.removeAllItems();
        cboToHop.addItem("Chọn tổ hợp");
        if (maNganh == null || maNganh.trim().isEmpty()) return;
        try {
            List<NganhTohop> list = new NganhTohopDAO().findAll();
            for (NganhTohop nt : list) {
                if (nt.getMaNganh().equalsIgnoreCase(maNganh)) {
                    cboToHop.addItem(nt.getMaToHop());
                }
            }
            if (selectedToHop != null) {
                cboToHop.setSelectedItem(selectedToHop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double getSubjectScore(DiemThiSinh diem, String subjectCode, String method) {
        if (diem == null || subjectCode == null) return 0.0;
        String code = subjectCode.trim().toUpperCase();
        
        if ("PT2".equals(method)) {
            switch (code) {
                case "TO": return val(diem.getTo());
                case "LI": return val(diem.getLi());
                case "HO": return val(diem.getHo());
                case "SI": return val(diem.getSi());
                case "SU": return val(diem.getSu());
                case "DI": return val(diem.getDi());
                case "VA": return val(diem.getVa());
                case "TI": return val(diem.getTi());
                case "KTPL": return val(diem.getKtpl());
                case "NK1": return val(diem.getNk1());
                case "NK2": return val(diem.getNk2());
                case "CNCN": return val(diem.getCncn());
                case "CNNN": return val(diem.getCnnn());
                case "N1": 
                    double cc = val(diem.getN1_cc());
                    double th = val(diem.getN1_thi());
                    return Math.max(cc, th);
                default: return 0.0;
            }
        } else if ("PT3".equals(method)) {
            switch (code) {
                case "TO": return util.VsatConverter.convert("TO", diem.getTO_VS()).doubleValue();
                case "VA": return util.VsatConverter.convert("VA", diem.getVA_VS()).doubleValue();
                case "LI": return util.VsatConverter.convert("LI", diem.getLI_VS()).doubleValue();
                case "HO": return util.VsatConverter.convert("HO", diem.getHO_VS()).doubleValue();
                case "SI": return util.VsatConverter.convert("SI", diem.getSI_VS()).doubleValue();
                case "SU": return util.VsatConverter.convert("SU", diem.getSU_VS()).doubleValue();
                case "DI": return util.VsatConverter.convert("DI", diem.getDI_VS()).doubleValue();
                case "N1": return util.VsatConverter.convert("N1", diem.getN1_VS()).doubleValue();
                default: return 0.0;
            }
        } else if ("PT4".equals(method)) {
            switch (code) {
                case "TO": return val(diem.getTO_NL());
                case "VA": return val(diem.getVA_NL());
                case "LI": return val(diem.getLI_NL());
                case "HO": return val(diem.getHO_NL());
                case "SI": return val(diem.getSI_NL());
                case "SU": return val(diem.getSU_NL());
                case "DI": return val(diem.getDI_NL());
                case "N1": return val(diem.getN1_NL());
                default: return 0.0;
            }
        }
        return 0.0;
    }

    private static double val(BigDecimal bd) {
        return bd == null ? 0.0 : bd.doubleValue();
    }

    private void showDialog(int row) {
        boolean isEdit = row >= 0;
        
        NguyenVong nv;
        if (isEdit) {
            int idnv = (Integer) tableModel.getValueAt(row, COL_ID);
            nv = nvDAO.findById(idnv);
            if (nv == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nguyện vọng trong DB!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            nv = new NguyenVong();
            nv.setNvTt(1);
            nv.setTtPhuongthuc("PT2");
            nv.setNvKetqua("Chưa xét");
            nv.setDiemThxt(BigDecimal.ZERO);
            nv.setDiemCong(BigDecimal.ZERO);
            nv.setDiemUtqd(BigDecimal.ZERO);
            nv.setDiemXettuyen(BigDecimal.ZERO);
        }

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Sửa nguyện vọng" : "Thêm nguyện vọng",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(480, 560);
        d.setLocationRelativeTo(this);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(new EmptyBorder(16,20,16,20));
        body.setBackground(AppTheme.BG_PRIMARY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // ComboBox & Fields
        List<ThiSinh> candidates = ThiSinhDAO.getAllCandidates();
        JComboBox<String> cboCccd = new JComboBox<>();
        cboCccd.setFont(AppTheme.FONT_BODY);
        cboCccd.addItem("Chọn thí sinh");
        for (ThiSinh ts : candidates) {
            cboCccd.addItem(ts.getCccd() + " - " + ts.getHo() + " " + ts.getTen());
        }

        List<Nganh> majors = new NganhDAO().findAll();
        JComboBox<String> cboNganh = new JComboBox<>();
        cboNganh.setFont(AppTheme.FONT_BODY);
        cboNganh.addItem("Chọn ngành");
        for (Nganh n : majors) {
            cboNganh.addItem(n.getMaNganh() + " - " + n.getTenNganh());
        }

        JComboBox<String> cboToHop = new JComboBox<>();
        cboToHop.setFont(AppTheme.FONT_BODY);
        cboToHop.addItem("Chọn tổ hợp");

        JComboBox<String> cboMethod = new JComboBox<>(new String[]{"PT2 - THPT", "PT3 - VSAT", "PT4 - DGNL"});
        cboMethod.setFont(AppTheme.FONT_BODY);

        JTextField txtRank = UIComponents.formField(isEdit ? String.valueOf(nv.getNvTt()) : "1");
        
        JComboBox<String> cboKetquaDialog = new JComboBox<>(new String[]{"Chưa xét", "Trúng tuyển", "Dưới sàn"});
        cboKetquaDialog.setFont(AppTheme.FONT_BODY);
        cboKetquaDialog.setSelectedItem(isEdit ? nv.getNvKetqua() : "Chưa xét");

        JTextField txtHoTen = UIComponents.formField(""); txtHoTen.setEditable(false);
        JTextField txtDiemToHop = UIComponents.formField("0.0000"); txtDiemToHop.setEditable(false);
        JTextField txtDiemCong = UIComponents.formField("0.0000"); txtDiemCong.setEditable(false);
        JTextField txtDiemUt = UIComponents.formField("0.0000"); txtDiemUt.setEditable(false);
        JTextField txtDiemXt = UIComponents.formField("0.0000"); txtDiemXt.setEditable(false);
        JTextField txtDoLech = UIComponents.formField("0.00"); txtDoLech.setEditable(false);

        // Map initial selections if editing
        if (isEdit) {
            if (nv.getNnCccd() != null) {
                for (int i = 0; i < cboCccd.getItemCount(); i++) {
                    if (cboCccd.getItemAt(i).startsWith(nv.getNnCccd())) {
                        cboCccd.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (nv.getNvManganh() != null) {
                for (int i = 0; i < cboNganh.getItemCount(); i++) {
                    if (cboNganh.getItemAt(i).startsWith(nv.getNvManganh())) {
                        cboNganh.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (nv.getTtPhuongthuc() != null) {
                if (nv.getTtPhuongthuc().equalsIgnoreCase("PT3")) cboMethod.setSelectedIndex(1);
                else if (nv.getTtPhuongthuc().equalsIgnoreCase("PT4")) cboMethod.setSelectedIndex(2);
                else cboMethod.setSelectedIndex(0);
            }
        }

        // Layout Dialog
        int rowIdx = 0;
        
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Chọn CCCD:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(cboCccd, gc);
        
        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Họ tên:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(txtHoTen, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Nguyện vọng số:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(txtRank, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Chọn ngành:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(cboNganh, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Tổ hợp xét tuyển:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(cboToHop, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Độ lệch tổ hợp:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(txtDoLech, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Phương thức xét:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(cboMethod, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Điểm tổ hợp:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(txtDiemToHop, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Điểm cộng:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(txtDiemCong, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Điểm ưu tiên:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(txtDiemUt, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Điểm xét tuyển:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(txtDiemXt, gc);

        rowIdx++;
        gc.gridx=0; gc.gridy=rowIdx; gc.weightx=0.35; body.add(UIComponents.formLabel("Kết quả xét:"), gc);
        gc.gridx=1; gc.weightx=0.65; body.add(cboKetquaDialog, gc);

        // Real-Time Recalculation Engine closure
        Runnable recalculate = () -> {
            try {
                String selectedCccdText = (String) cboCccd.getSelectedItem();
                if (selectedCccdText == null || selectedCccdText.equals("Chọn thí sinh")) {
                    txtHoTen.setText("");
                    txtDiemUt.setText("0.0000");
                    txtDiemToHop.setText("0.0000");
                    txtDiemCong.setText("0.0000");
                    txtDoLech.setText("0.00");
                    txtDiemXt.setText("0.0000");
                    return;
                }
                String cccd = selectedCccdText.split(" - ")[0].trim();
                ThiSinh ts = ThiSinhDAO.getCandidateByCCCD(cccd);
                if (ts != null) {
                    txtHoTen.setText(ts.getHo() + " " + ts.getTen());
                }

                // 1. Calculate Priority points (Điểm ưu tiên)
                double priorityPoints = 0.0;
                if (ts != null) {
                    String kv = ts.getKhuVuc() != null ? ts.getKhuVuc().trim().toUpperCase() : "";
                    if (kv.contains("KV1")) priorityPoints += 0.75;
                    else if (kv.contains("KV2NT") || kv.contains("KV2-NT")) priorityPoints += 0.50;
                    else if (kv.contains("KV2")) priorityPoints += 0.25;

                    String dt = ts.getDoiTuong() != null ? ts.getDoiTuong().trim() : "";
                    if (dt.equals("01") || dt.equals("02") || dt.equals("03") || dt.equals("04") ||
                        dt.startsWith("UT1") || dt.startsWith("UT2") || dt.startsWith("UT3") || dt.startsWith("UT4")) {
                        priorityPoints += 2.0;
                    } else if (dt.equals("05") || dt.equals("06") || dt.equals("07") ||
                               dt.startsWith("UT5") || dt.startsWith("UT6") || dt.startsWith("UT7")) {
                        priorityPoints += 1.0;
                    }
                }
                txtDiemUt.setText(String.format("%.4f", priorityPoints));

                // 2. Fetch Major-Combination settings & Deviation (Độ lệch)
                String selectedNganhText = (String) cboNganh.getSelectedItem();
                String maNganh = (selectedNganhText == null || selectedNganhText.equals("Chọn ngành")) 
                    ? "" : selectedNganhText.split(" - ")[0].trim();
                    
                String tohop = (String) cboToHop.getSelectedItem();
                if (tohop == null || tohop.equals("Chọn tổ hợp")) tohop = "";
                
                double doLech = 0.0;
                NganhTohop nt = null;
                if (!maNganh.isEmpty() && !tohop.isEmpty()) {
                    nt = new NganhTohopDAO().findByMaNganhAndMaToHop(maNganh, tohop);
                    if (nt != null && nt.getDoLech() != null) {
                        doLech = nt.getDoLech().doubleValue();
                    }
                }
                txtDoLech.setText(String.format("%.2f", doLech));

                // 3. Method
                String selectedMethodText = (String) cboMethod.getSelectedItem();
                String method = "PT2";
                if (selectedMethodText != null) {
                    if (selectedMethodText.contains("PT3")) method = "PT3";
                    else if (selectedMethodText.contains("PT4")) method = "PT4";
                }

                // 4. Calculate Combination Score
                double thScore = 0.0;
                if (ts != null && nt != null) {
                    DiemThiSinh diem = DiemThiSinhDAO.getCandidateScoreByCCCD(cccd);
                    if (diem != null) {
                        double s1 = getSubjectScore(diem, nt.getMon1(), method);
                        double s2 = getSubjectScore(diem, nt.getMon2(), method);
                        double s3 = getSubjectScore(diem, nt.getMon3(), method);
                        int h1 = nt.getHeSo1() != null ? nt.getHeSo1() : 1;
                        int h2 = nt.getHeSo2() != null ? nt.getHeSo2() : 1;
                        int h3 = nt.getHeSo3() != null ? nt.getHeSo3() : 1;
                        thScore = s1 * h1 + s2 * h2 + s3 * h3;
                    }
                }
                txtDiemToHop.setText(String.format("%.4f", thScore));

                // 5. Fetch Bonus points from DiemCong table
                double bonusPoints = 0.0;
                if (!tohop.isEmpty()) {
                    DiemCong dc = new DiemCongDAO().findByCccdToHopAndMethod(cccd, tohop, method);
                    if (dc != null && dc.getDiemTong() != null) {
                        bonusPoints = dc.getDiemTong().doubleValue();
                    }
                }
                txtDiemCong.setText(String.format("%.4f", bonusPoints));

                // 6. Final Admission Score (Tổng điểm xét tuyển)
                double xtScore = thScore + doLech + priorityPoints + bonusPoints;
                txtDiemXt.setText(String.format("%.4f", xtScore));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        // Attach listeners
        cboCccd.addActionListener(e -> recalculate.run());
        cboMethod.addActionListener(e -> recalculate.run());
        cboKetquaDialog.addActionListener(e -> recalculate.run());
        cboToHop.addActionListener(e -> recalculate.run());

        cboNganh.addActionListener(e -> {
            String selectedNganhText = (String) cboNganh.getSelectedItem();
            String maNganh = (selectedNganhText == null || selectedNganhText.equals("Chọn ngành")) 
                ? "" : selectedNganhText.split(" - ")[0].trim();
            updateToHopOptions(maNganh, cboToHop, null);
            recalculate.run();
        });

        // Initialize values
        if (isEdit && nv.getNvManganh() != null) {
            updateToHopOptions(nv.getNvManganh(), cboToHop, nv.getTtThm());
        }
        recalculate.run();

        // Footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(AppTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER));
        RoundButton cancel = RoundButton.secondary("Hủy");
        RoundButton save   = RoundButton.primary("Lưu NV");
        cancel.addActionListener(e -> d.dispose());
        
        save.addActionListener(e -> {
            try {
                String selectedCccdText = (String) cboCccd.getSelectedItem();
                if (selectedCccdText == null || selectedCccdText.equals("Chọn thí sinh")) {
                    JOptionPane.showMessageDialog(d, "Vui lòng chọn thí sinh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String cccd = selectedCccdText.split(" - ")[0].trim();

                String selectedNganhText = (String) cboNganh.getSelectedItem();
                if (selectedNganhText == null || selectedNganhText.equals("Chọn ngành")) {
                    JOptionPane.showMessageDialog(d, "Vui lòng chọn ngành!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String maNganh = selectedNganhText.split(" - ")[0].trim();

                String tohop = (String) cboToHop.getSelectedItem();
                if (tohop == null || tohop.equals("Chọn tổ hợp") || tohop.isEmpty()) {
                    JOptionPane.showMessageDialog(d, "Vui lòng chọn tổ hợp môn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int rank;
                try {
                    rank = Integer.parseInt(txtRank.getText().trim());
                    if (rank <= 0) throw new Exception();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(d, "Thứ tự nguyện vọng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check for aspiration rank duplicate
                NguyenVong duplicate = nvDAO.findByCccdAndNvTt(cccd, rank);
                if (duplicate != null && duplicate.getIdnv() != nv.getIdnv()) {
                    JOptionPane.showMessageDialog(d, "Thí sinh đã đăng ký nguyện vọng thứ " + rank + " rồi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedMethodText = (String) cboMethod.getSelectedItem();
                String method = "PT2";
                if (selectedMethodText != null) {
                    if (selectedMethodText.contains("PT3")) method = "PT3";
                    else if (selectedMethodText.contains("PT4")) method = "PT4";
                }

                // Map state to entity
                nv.setNnCccd(cccd);
                nv.setNvManganh(maNganh);
                nv.setNvTt(rank);
                nv.setTtThm(tohop);
                nv.setTtPhuongthuc(method);
                nv.setNvKetqua((String) cboKetquaDialog.getSelectedItem());
                
                nv.setDiemThxt(new BigDecimal(txtDiemToHop.getText()));
                nv.setDiemCong(new BigDecimal(txtDiemCong.getText()));
                nv.setDiemUtqd(new BigDecimal(txtDiemUt.getText()));
                nv.setDiemXettuyen(new BigDecimal(txtDiemXt.getText()));
                
                nv.setNvKeys(cccd + "_" + rank);

                nvDAO.saveOrUpdate(nv);
                JOptionPane.showMessageDialog(d, "Lưu nguyện vọng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                d.dispose();
                refreshData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Lỗi khi lưu dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(cancel); footer.add(save);
        d.setLayout(new BorderLayout());
        d.add(new JScrollPane(body), BorderLayout.CENTER);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showImport() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import — Nguyện vọng");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel", "xlsx","xls"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            JOptionPane.showMessageDialog(this, "Sẽ import danh sách nguyện vọng.", "Import", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Nút Sửa / Xóa trên từng dòng ---

    class ActionPanelRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnSua = actionButton("Sửa", false);
        private final JButton btnXoa = actionButton("Xóa", true);

        ActionPanelRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 2));
            setOpaque(true);
            add(btnSua);
            add(btnXoa);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value,
                boolean selected, boolean focus, int row, int column) {
            setBackground(selected ? tbl.getSelectionBackground()
                : (row % 2 == 0 ? AppTheme.BG_PRIMARY : AppTheme.BG_SECONDARY));
            return this;
        }
    }

    class ActionPanelEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        private final JButton btnSua = actionButton("Sửa", false);
        private final JButton btnXoa = actionButton("Xóa", true);
        private int editingRow = -1;

        ActionPanelEditor() {
            panel.setOpaque(true);
            btnSua.addActionListener(e -> {
                fireEditingStopped();
                if (editingRow >= 0) {
                    showDialog(editingRow);
                }
            });
            btnXoa.addActionListener(e -> {
                fireEditingStopped();
                if (editingRow >= 0) {
                    confirmDelete(editingRow);
                }
            });
            panel.add(btnSua);
            panel.add(btnXoa);
        }

        @Override
        public Component getTableCellEditorComponent(JTable tbl, Object value,
                boolean selected, int row, int column) {
            editingRow = row;
            panel.setBackground(tbl.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private static JButton actionButton(String text, boolean danger) {
        JButton b = new JButton(text);
        b.setFont(AppTheme.FONT_SMALL);
        b.setFocusPainted(false);
        if (danger) {
            b.setBackground(AppTheme.RED_LIGHT);
            b.setForeground(AppTheme.RED);
            b.setBorder(BorderFactory.createLineBorder(AppTheme.RED_LIGHT));
        } else {
            b.setBackground(AppTheme.BG_SECONDARY);
            b.setForeground(AppTheme.TEXT_PRIMARY);
            b.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        }
        return b;
    }

    static class PhuongThucRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if ("PT4".equals(v) || "ĐGNL".equals(v)) {
                setText("ĐGNL");
                setForeground(AppTheme.PRIMARY);
                setBackground(AppTheme.PRIMARY_LIGHT);
            } else if ("PT2".equals(v) || "THPT".equals(v)) {
                setText("THPT");
                setForeground(AppTheme.GREEN);
                setBackground(AppTheme.GREEN_LIGHT);
            } else if ("PT3".equals(v) || "VSAT".equals(v)) {
                setText("VSAT");
                setForeground(AppTheme.AMBER);
                setBackground(AppTheme.AMBER_LIGHT);
            } else {
                setText(v != null ? v.toString() : "");
                setForeground(AppTheme.TEXT_SECOND);
                setBackground(AppTheme.BG_SECONDARY);
            }
            if (sel)
                setBackground(AppTheme.PRIMARY_LIGHT);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(AppTheme.FONT_SMALL);
            return this;
        }
    }
}