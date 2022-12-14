package hardy;

// GEN-P

import umlp.rte.DatabaseAccessManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// Provides methods to change database entries related to the Person class
// There should be a small amount of instances that is not related to the number of Person instances
public class HumanDAO {
  protected PreparedStatement setAgeStatement = null;

  public void updateAge(long gemId, int age) {
    if(setAgeStatement == null){
      setAgeStatement = DatabaseAccessManager.prepareStatement("UPDATE human SET age = ? WHERE id = ?");
    }

    try {
      setAgeStatement.clearParameters();
      setAgeStatement.setInt(1, age);
      setAgeStatement.setLong(2, gemId);
      setAgeStatement.execute();
    }catch (SQLException e){
      // TODO: how do we handle sql errors
      throw new RuntimeException(e);
    }
  }
}
