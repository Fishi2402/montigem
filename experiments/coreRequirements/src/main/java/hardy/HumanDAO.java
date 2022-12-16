package hardy;

// GEN-P

import umlp.rte.DatabaseAccessManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Provides methods to change database entries related to the Person class
// There should be a small amount of instances that is not related to the number of Person instances
public class HumanDAO {
  // TODO: Optimization: bulk operations
  // TODO: return values for methods?
  // TODO: type safety is low since some methods get input long gemId instead of Human,
  // which allows them to be used without having the object loaded(e.g. in context of a GemList)
  protected PreparedStatement insertHuman = null;
  protected PreparedStatement selectHuman = null;
  protected PreparedStatement deleteHuman = null;
  protected PreparedStatement setAgeStatement = null;

  protected PreparedStatement selectAllHumanIds = null;

  public void insertHuman(Human human){
    try{
      if(insertHuman == null || insertHuman.isClosed()){
        insertHuman = DatabaseAccessManager.prepareStatement("INSERT INTO human (id, age) VALUES (?, ?)");
      }

      insertHuman.clearParameters();
      insertHuman.setLong(1, human.getGemId());
      insertHuman.setInt(2, human.getAge());
      insertHuman.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public Optional<Human> selectHuman(long gemId){
    try{
      if(selectHuman == null || selectHuman.isClosed()){
        selectHuman = DatabaseAccessManager.prepareStatement("SELECT id, age FROM human WHERE id = ?");
      }

      selectHuman.clearParameters();
      selectHuman.setLong(1, gemId);
      ResultSet res = selectHuman.executeQuery();
      if(res.next()){
        HumanBuilder builder = FlensburgManager.humanBuilder(gemId);
        builder.setINTERNAL_deser_mode(true);
        builder.age(res.getInt("age"));
        return builder.build();
      }else {
        return Optional.empty();
      }
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public void deleteHuman(long gemId){
    try{
      if(deleteHuman == null || deleteHuman.isClosed()){
        deleteHuman = DatabaseAccessManager.prepareStatement("DELETE FROM human WHERE id = ?");
      }

      deleteHuman.clearParameters();
      deleteHuman.setLong(1, gemId);
      deleteHuman.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public void updateAge(long gemId, int age) {
    try {
      if(setAgeStatement == null || setAgeStatement.isClosed()){
        setAgeStatement = DatabaseAccessManager.prepareStatement("UPDATE human SET age = ? WHERE id = ?");
      }

      setAgeStatement.clearParameters();
      setAgeStatement.setInt(1, age);
      setAgeStatement.setLong(2, gemId);
      setAgeStatement.execute();
    }catch (SQLException e){
      // TODO: how do we handle sql errors
      throw new RuntimeException(e);
    }
  }

  public List<Long> selectAllHumanIds() {
    if(selectAllHumanIds == null){
      selectAllHumanIds = DatabaseAccessManager.prepareStatement("SELECT id FROM human;");
    }

    try {
      List<Long> res = new ArrayList<>();
      ResultSet rs = selectAllHumanIds.executeQuery();
      while (rs.next()){
        res.add(rs.getLong(1));
      }
      return res;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
