package hardy;

// GEN-P

// vgl. HumanDbObserver für Erklärung
public class PersonDbObserver implements PersonObserver{
  protected static PersonDAO dao = new PersonDAO();

  @Override
  public void notifySetAge(Human h, int age) {
    dao.updateAge(h.getGemId(), age);
  }

  @Override
  public void notifySetNickname(Person p, String nickname) {
    dao.updateNickname(p.getGemId(), nickname);
  }

  @Override
  public void notifyAddVisited(Person p, long addressGemId, int indexInList) {
    dao.addVisited(p.getGemId(), addressGemId, indexInList);
  }

  @Override
  public void notifyRemoveVisited(Person p, long addressGemId, int indexInList) {
    dao.removeVisited(p.getGemId(), addressGemId, indexInList);
  }

  @Override
  public void notifySetVisited(Person person, Long oldAddressGemId, long addressGemId, int index) {
    dao.updateVisited(person.getGemId(), oldAddressGemId, addressGemId, index);
  }
}
