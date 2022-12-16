package hardy;

// GEN-P
// See HumanDbObserver for an explanation
public class AddressDbObserver implements AddressObserver{
  protected static AddressDAO dao = new AddressDAO();
  protected static AssocVisitsDAO assocVisitsDAO = new AssocVisitsDAO();

  @Override
  public void notifyAddVisitor(Address address, long gemId, int indexInList) {
    // TODO: there are two indices in the corresponding list but only one argument!
    assocVisitsDAO.addVisits(gemId, address.getGemId(), indexInList);
  }

  @Override
  public void notifyRemoveVisitor(Address address, long gemId, int indexInList) {
    assocVisitsDAO.removeVisits(gemId, address.getGemId(), indexInList);
  }
}
