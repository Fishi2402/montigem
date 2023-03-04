/* (c) https://github.com/MontiCore/monticore */
package hardy;

import umlp.backendrte.common.DatabaseAccessManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AssocMainResidenceDAO {
  protected PreparedStatement insertMainResidence = null;
  protected PreparedStatement removeMainResidence = null;
  protected PreparedStatement selectAddressIdForPerson = null;

  private void ensureInsertMainResidenceCreated() {
    if(insertMainResidence == null){
      insertMainResidence = DatabaseAccessManager.prepareStatement("INSERT INTO assoc_main_residence (person_id,  address_id) VALUES (?, ?)");
    }
  }

  public void insertMainResidence(long personGemId, long addressGemId, int indexInList) {
    // TODO: use index
    ensureInsertMainResidenceCreated();

    try {
      insertMainResidence.clearParameters();
      insertMainResidence.setLong(1, personGemId);
      insertMainResidence.setLong(2, addressGemId);
      insertMainResidence.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public Optional<Long> selectAddressIdForPerson(long personId){
    ensureSelectAddressIdForPersonCreated();

    try {
      selectAddressIdForPerson.clearParameters();
      selectAddressIdForPerson.setLong(1, personId);
      Optional<Long> res = Optional.empty();
      ResultSet rs = selectAddressIdForPerson.executeQuery();
      if(rs.next()){
        res = Optional.of(rs.getLong(1));
      }
      return res;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void ensureSelectAddressIdForPersonCreated(){
    if(selectAddressIdForPerson == null){
      selectAddressIdForPerson = DatabaseAccessManager.prepareStatement("SELECT address_id FROM assoc_main_residence WHERE person_id = ?;");
    }
  }

  public void removeMainResidence(long gemId, long addressGemId, int indexInList) {
    // TODO: use index
    ensureRemoveMainResidenceCreated();

    try {
      removeMainResidence.clearParameters();
      removeMainResidence.setLong(1, gemId);
      removeMainResidence.setLong(2, addressGemId);
      removeMainResidence.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  private void ensureRemoveMainResidenceCreated() {
    if(removeMainResidence == null){
      removeMainResidence = DatabaseAccessManager.prepareStatement("DELETE FROM assoc_main_residence WHERE person_id = ? AND address_id = ?");
    }
  }
}
