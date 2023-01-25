/* (c) https://github.com/MontiCore/monticore */
package hardy;

public class FlensburgDbObserver implements FlensburgObserver {
  HumanDAO humanDAO = new HumanDAO();
  PersonDAO personDAO = new PersonDAO();
  AddressDAO addressDAO = new AddressDAO();
  // TODO: other DAOs


  @Override
  public void notifyHumanAdded(Human human) {
    humanDAO.insertHuman(human);
  }

  @Override
  public void notifyHumanDeleted(Human human) {
    humanDAO.deleteHuman(human.getGemId());
  }

  @Override
  public void notifyPersonAdded(Person person) {
    personDAO.insertPerson(person);
  }

  @Override
  public void notifyAddressAdded(Address address) {
    addressDAO.insertAddress(address);
  }

  // TODO: Others, maybe generate
}
