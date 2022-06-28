package hardy;

// GEN-P

import org.hibernate.Session;
import umlp.rte.DatabaseAccessManager;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

// vgl. HumanDbObserver für Erklärung
public class PersonDbObserver implements PersonObserver{

    @Override
  public void notifySetAge(Human h, int age) {
    Session session = DatabaseAccessManager.getSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaUpdate<Person> update = builder.createCriteriaUpdate(Person.class);
    Root<Person> root = update.from(Person.class);
    update.set(root.get("age"), age);
    update.where(builder.equal(root.get("gemId"), h.getGemId()));
    session.createQuery(update).executeUpdate();

    // TODO: error handling
  }

  @Override
  public void notifySetNickname(Person p, String nickname) {
    Session session = DatabaseAccessManager.getSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaUpdate<Person> update = builder.createCriteriaUpdate(Person.class);
    Root<Person> root = update.from(Person.class);
    update.set(root.get("nickname"), nickname);
    update.where(builder.equal(root.get("gemId"), p.getGemId()));
    session.createQuery(update).executeUpdate();
  }
}
