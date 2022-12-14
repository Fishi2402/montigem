package hardy;

// GEN-P

// vgl. HumanDbObserver für Erklärung
public class PersonDbObserver implements PersonObserver{

    @Override
  public void notifySetAge(Human h, int age) {
    // TODO: impl
  }

  @Override
  public void notifySetNickname(Person p, String nickname) {
    // TODO: impl
  }

  @Override
  public void notifyRemoveVisited(Person p, long addressGemId, int indexInList) {
    // TODO: needed?
  }

  @Override
  public void notifySetVisited(Person person, Long oldAddressGemId, long addressGemId, int index) {
    // TODO: needed?
  }
}
