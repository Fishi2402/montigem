package hardy;

// GEN-P

// A HumanObserver that synchronizes changes of a Human domain object to the database
// Since all data synchronization should be handled by observers, the JPA transaction style update can not be used.
// Instead an update query is used
public class HumanDbObserver implements HumanObserver{

  @Override
  public void notifySetAge(Human h, int age) {
    // TODO: impl
  }
}
