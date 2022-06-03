package hardy;

// GEN-P

import org.hibernate.Session;
import umlp.rte.DatabaseAccessManager;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

// A HumanObserver that synchronizes changes of a Human domain object to the database
// Since all data synchronization should be handled by observers, the JPA transaction style update can not be used.
// Instead an update query is used
public class HumanDbObserver implements HumanObserver{

  @Override
  public void notifySetAge(Human h, int age) {
    Session session = DatabaseAccessManager.getSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaUpdate<Human> update = builder.createCriteriaUpdate(Human.class);
    Root<Human> root = update.from(Human.class);
    update.set(root.get("age"), age);
    update.where(builder.equal(root.get("gemId"), h.getGemId()));
    session.createQuery(update).executeUpdate();

    // TODO: error handling
  }
}
