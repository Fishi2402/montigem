package hardy;

public class FlensburgDbObserver implements FlensburgObserver {
  HumanDAO humanDAO = new HumanDAO();
  PersonDAO personDAO = new PersonDAO();
  AddressDAO addressDAO = new AddressDAO();
  // TODO: other DAOs


  @Override
  public void notifyHumanAdded(long gemId) {
    humanDAO.insertHuman(FlensburgManager.getHumanSure(gemId));
  }

  @Override
  public void notifyHumanDeleted(long gemId) {
    humanDAO.deleteHuman(gemId);
  }

  @Override
  public void notifyPersonAdded(long gemId) {
    personDAO.insertPerson(FlensburgManager.getPersonSure(gemId));
  }

  @Override
  public void notifyAddressAdded(long gemId) {
    addressDAO.insertAddress(FlensburgManager.getAddressSure(gemId));
  }

  // TODO: Others, maybe generate
}
