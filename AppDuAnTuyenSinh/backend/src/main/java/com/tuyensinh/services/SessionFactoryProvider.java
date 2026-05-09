package com.tuyensinh.services;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryProvider {
    public static SessionFactory provideSessionFactory() {
        return new Configuration().configure().buildSessionFactory();
    }
}