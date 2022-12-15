package hardy;

// GEN-P

// A HumanObserver that synchronizes changes of a Human domain object to the database
public class HumanDbObserver implements HumanObserver{
  protected static HumanDAO dao = new HumanDAO();

  // Insert and delete are implemented in FlensburgDbObserver

  @Override
  public void notifySetAge(Human h, int age) {
    dao.updateAge(h.getGemId(), age);
  }
}
