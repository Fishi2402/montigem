/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P

// vgl. HumanDbObserver für Erklärung
public class PersonDbObserver implements PersonObserver{
  protected static PersonDAO dao = new PersonDAO();
  protected static AssocVisitsDAO assocVisitsDAO = new AssocVisitsDAO();

  // Insert and delete are implemented in FlensburgDbObserver

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
    assocVisitsDAO.addVisits(p.getGemId(), addressGemId, indexInList);
  }

  @Override
  public void notifyRemoveVisited(Person p, long addressGemId, int indexInList) {
    assocVisitsDAO.removeVisits(p.getGemId(), addressGemId, indexInList);
  }

  @Override
  public void notifySetVisited(Person person, Long oldAddressGemId, long addressGemId, int index) {
    assocVisitsDAO.updateVisits(person.getGemId(), oldAddressGemId, addressGemId, index);
  }
}
