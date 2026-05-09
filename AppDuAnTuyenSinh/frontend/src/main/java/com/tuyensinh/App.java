package com.tuyensinh;

import com.formdev.flatlaf.FlatDarkLaf;
import com.tuyensinh.views.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App {
    public static void main(String[] args) {
        // Thiết lập giao diện FlatLaf Dark Mode siêu mượt
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Không thể khởi tạo giao diện FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            com.tuyensinh.views.LoginFrame login = new com.tuyensinh.views.LoginFrame();
            login.setVisible(true);
        });

        try {
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            System.out.println("Hibernate setup successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
