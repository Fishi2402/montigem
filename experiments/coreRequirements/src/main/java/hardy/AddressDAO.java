/* (c) https://github.com/MontiCore/monticore */
package hardy;

import umlp.rte.AssociationDBEntry;
import umlp.rte.DatabaseAccessManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddressDAO {
  protected PreparedStatement insertAddress = null;
  protected PreparedStatement selectAddress = null;
  protected PreparedStatement deleteAddress = null;
  protected PreparedStatement setCityStatement = null;

  AssocVisitsDAO assocVisitsDAO = new AssocVisitsDAO();

  public void insertAddress(Address address){
    try{
      ensureInsertAddressCreated();

      insertAddress.clearParameters();
      insertAddress.setLong(1, address.getGemId());
      insertAddress.setString(2, address.getCity());
      insertAddress.execute();

      List<Long> iDs = address.getVisitorGemList().getIDs();

      // Bulk operation => only one call to db
      List<AssociationDBEntry> entries = new ArrayList<>();
      for (int i = 0; i < iDs.size(); i++) {
        Long visitorId = iDs.get(i);
        entries.add(new AssociationDBEntry(visitorId, address.getGemId(), i));
      }
      assocVisitsDAO.addAllVisits(entries);
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  private void ensureInsertAddressCreated() {
    if(insertAddress == null){
      insertAddress = DatabaseAccessManager.prepareStatement("INSERT INTO address (id, city) VALUES (?, ?)");
    }
  }

  public Optional<Address> selectAddress(long gemId){
    try{
      ensureSelectAddressCreated();

      selectAddress.clearParameters();
      selectAddress.setLong(1, gemId);
      ResultSet res = selectAddress.executeQuery();
      AddressBuilder builder = FlensburgManager.addressBuilder(gemId);
      if(res.next()){
        builder.setINTERNAL_deser_mode(true);
        builder.city(res.getString("city"));
      }else {
        return Optional.empty();
      }

      builder.visitorGemListIds(assocVisitsDAO.selectVisitorIdsForVisited(gemId));
      return builder.build();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  private void ensureSelectAddressCreated() throws SQLException {
    if(selectAddress == null){
      selectAddress = DatabaseAccessManager.prepareStatement("SELECT id, city FROM address WHERE id = ?");
    }
  }

  public void deleteAddress(long gemId){
    try{
      ensureDeleteAddressCreated();

      deleteAddress.clearParameters();
      deleteAddress.setLong(1, gemId);
      deleteAddress.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  private void ensureDeleteAddressCreated() {
    if(deleteAddress == null ){
      deleteAddress = DatabaseAccessManager.prepareStatement("DELETE FROM address WHERE id = ?");
    }
  }

  public void updateCity(long gemId, String city) {
    try {
      ensureUpdateCityCreated();

      setCityStatement.clearParameters();
      setCityStatement.setString(1, city);
      setCityStatement.setLong(2, gemId);
      setCityStatement.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  private void ensureUpdateCityCreated() {
    if(setCityStatement == null){
      setCityStatement = DatabaseAccessManager.prepareStatement("UPDATE address SET city = ? WHERE id = ?");
    }
  }
}
