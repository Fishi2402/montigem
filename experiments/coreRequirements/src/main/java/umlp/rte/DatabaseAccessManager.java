package umlp.rte;

import hardy.Address;
import hardy.FlensburgManager;
import hardy.FlensburgManagerImpl;
import hardy.Human;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class DatabaseAccessManager {

    public static void main(String[] args) {
        FlensburgManagerImpl.init();

        FlensburgManager.humanBuilder().age(16).build();
        FlensburgManager.humanBuilder().age(17).build();
        FlensburgManager.humanBuilder().age(18).build();
        FlensburgManager.humanBuilder().age(19).build();
        FlensburgManager.humanBuilder().age(20).build();
        FlensburgManager.humanBuilder().age(21).build();

        FlensburgManager.addressBuilder().city("Aachen").build();
        FlensburgManager.addressBuilder().city("Düsseldorf").build();
        FlensburgManager.addressBuilder().city("Berlin").build();
        FlensburgManager.addressBuilder().city("München").build();
        FlensburgManager.addressBuilder().city("Flensburg").build();
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
        return 0; // TODO implement
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
    }
}
