package br.ufrj.dcc.tesi.utils;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class MySQLUtil {
        
        private static MySQLUtil instance;
        
        private Session session;
        
        public static void main(String[] args){
                Session session = MySQLUtil.getInstance().getSession();
                session.close();
        }
        
        public MySQLUtil(){
        this.session = connect();
        }

        public Session connect() {
                System.out.println("Trying to create a test connection with the database.");
        try {
                        Configuration configuration = new Configuration().configure();
                        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                        SessionFactory sessionFactory = configuration.buildSessionFactory(ssrb.build());
                        Session session = sessionFactory.openSession();
                        System.out.println("Test connection with the database created successfuly.");
                        return session;
                } catch (HibernateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return null;
        }
        
        public static MySQLUtil getInstance(){
                if(instance != null) return instance;
                else {
                        instance = new MySQLUtil();
                        return instance;
                }
        }

        public Session getSession() {
                return session;
        }

        public void setSession(Session session) {
                this.session = session;
        }

}