package umlp.rte;

import hardy.Car;
import hardy.FlensburgManager;
import hardy.FlensburgManagerImpl;
import hardy.Human;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseAccessManager {

    public static void main(String[] args) {
        FlensburgManagerImpl.init();

        FlensburgManager.humanBuilder().age(16).build();
        FlensburgManager.humanBuilder().age(17).build();
        FlensburgManager.humanBuilder().age(18).build();
        FlensburgManager.humanBuilder().age(19).build();
        FlensburgManager.humanBuilder().age(20).build();
        FlensburgManager.humanBuilder().age(21).build();


        FlensburgManager.carBuilder().build();
        FlensburgManager.carBuilder().build();
        FlensburgManager.carBuilder().build();
        FlensburgManager.carBuilder().build();
        FlensburgManager.carBuilder().build();
        FlensburgManager.carBuilder().build();

    }

    private static final String DATABASE_PROPERTIES = "database.properties";

    private static DatabaseAccessManager instance;

    private Properties databaseProperties;

    private SessionFactory sessionFactory;

    /**
     * Don't let anyone else instantiate this class.
     */
    private DatabaseAccessManager() {
    }

    public static DatabaseAccessManager getSingleton() {
        if (instance == null) {
            instance = new DatabaseAccessManager();
        }
        return instance;
    }

    public static void setup() {
        getSingleton()._setup();
    }

    public static <T> T getObject(Class<T> domainType, long gemId) {
        return getSingleton()._getObject(domainType, gemId);
    }

    public static void addObject(String domainType, Object domainObject) {
        getSingleton()._addObject(domainType, domainObject);
    }

    public static void deleteObject(String domainType, Object domainObject) {
        getSingleton()._deleteObject(domainType, domainObject);
    }

    public static long getFreeGemId() {
        return 0;
    }

    public <T> T _getObject(Class<T> domainType, long gemId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        T object = session.get(domainType, gemId);
        session.getTransaction().commit();
        session.close();
        return object;
    }

    public void _addObject(String domainType, Object domainObject) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(domainType, domainObject);
        session.getTransaction().commit();
        session.close();
    }

    private void _deleteObject(String domainType, Object domainObject) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(domainType, domainObject);
        session.getTransaction().commit();
        session.close();
    }

    private void _setup() {
        loadDatabaseProperties();
        createSessionFactory();
    }

    private void createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.addProperties(databaseProperties);
        configuration.addAnnotatedClass(Human.class);
        configuration.addAnnotatedClass(Car.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    private void loadDatabaseProperties() {
        databaseProperties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = loader.getResourceAsStream(DATABASE_PROPERTIES)) {
            databaseProperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
