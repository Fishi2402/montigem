package hardy;

import umlp.rte.DatabaseAccessManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AddressDAO {
  protected PreparedStatement insertAddress = null;
  protected PreparedStatement selectAddress = null;
  protected PreparedStatement deleteAddress = null;
  protected PreparedStatement setCityStatement = null;

  public void insertAddress(Address address){
    try{
      if(insertAddress == null || insertAddress.isClosed()){
        insertAddress = DatabaseAccessManager.prepareStatement("INSERT INTO address (id, city) VALUES (?, ?)");
      }

      insertAddress.clearParameters();
      insertAddress.setLong(1, address.getGemId());
      insertAddress.setString(2, address.getCity());
      insertAddress.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public Optional<Address> selectAddress(long gemId){
    try{
      if(selectAddress == null || selectAddress.isClosed()){
        selectAddress = DatabaseAccessManager.prepareStatement("SELECT id, city FROM address WHERE id = ?");
      }

      selectAddress.clearParameters();
      selectAddress.setLong(1, gemId);
      ResultSet res = selectAddress.executeQuery();
      if(res.next()){
        AddressBuilder builder = FlensburgManager.addressBuilder(gemId);
        builder.setINTERNAL_deser_mode(true);
        builder.city(res.getString("city"));
        return builder.build();
      }else {
        return Optional.empty();
      }
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public void deleteAddress(long gemId){
    try{
      if(deleteAddress == null || deleteAddress.isClosed()){
        deleteAddress = DatabaseAccessManager.prepareStatement("DELETE FROM address WHERE id = ?");
      }

      deleteAddress.clearParameters();
      deleteAddress.setLong(1, gemId);
      deleteAddress.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public void updateCity(long gemId, String city) {
    try {
      if(setCityStatement == null || setCityStatement.isClosed()){
        setCityStatement = DatabaseAccessManager.prepareStatement("UPDATE address SET city = ? WHERE id = ?");
      }

      setCityStatement.clearParameters();
      setCityStatement.setString(1, city);
      setCityStatement.setLong(2, gemId);
      setCityStatement.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }
}
