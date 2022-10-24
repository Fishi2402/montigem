package umlp.rte;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

public class DatabaseAccessManager {

    static protected long freeGemId = 1001L;

    public static void init(String[] args) {

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

    public static void setup(Set<Class> annotatedClasses) {
        getSingleton()._setup(annotatedClasses);
    }

    public static long getFreeGemId() {
        return freeGemId++; // TODO implement / discuss if we want to use generated IDs from Spring/ Hibernate
    }

    public static <T> T get(Class<T> domainType, long gemId) {
        return getSingleton()._get(domainType, gemId);
    }

    public static void add(String domainType, Object domainObject) {
        getSingleton()._add(domainType, domainObject);
    }

    public static void delete(String domainType, Object domainObject) {
        getSingleton()._delete(domainType, domainObject);
    }

    public static <T> Collection<T> getAll(Class<T> domainType) {
        return getSingleton()._getAll(domainType);
    }

    public static Session getSession(){
        return getSingleton()._getSession();
    }

    protected Session _getSession() {
        return sessionFactory.openSession();
    }

    private <T> T _get(Class<T> domainType, long gemId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        T object = session.get(domainType, gemId);
//        CriteriaBuilder builder = session.getCriteriaBuilder();
//        CriteriaQuery<T> query = builder.createQuery(domainType);
//        Root<T> root = query.from(domainType);
//        query.where(builder.equal(root.get("gemId"), gemId));
//        T result = session.createQuery(query).getSingleResult();
        session.getTransaction().commit();
        session.close();
        return object;
    }

    private void _add(String domainType, Object domainObject) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(domainType, domainObject);
        session.getTransaction().commit();
        session.close();
    }

    private void _delete(String domainType, Object domainObject) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(domainType, domainObject);
        session.getTransaction().commit();
        session.close();
    }

    private <T> Collection<T> _getAll(Class<T> domainType) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainType);
        query.from(domainType);
        Collection<T> result = session.createQuery(query).list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    private void _setup(Set<Class> annotatedClasses) {
        loadDatabaseProperties();
        createSessionFactory(annotatedClasses);
    }

    private void createSessionFactory(Set<Class> annotatedClasses) {
        Configuration configuration = new Configuration();
        configuration.addProperties(databaseProperties);
        annotatedClasses.forEach(aClass -> configuration.addAnnotatedClass(aClass));
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

        // Load Properties from Environment Variables
        // Can be used to select a different DB on production server
        // Environment Properties have priority!
        databaseProperties.putAll(System.getenv());
    }
}
