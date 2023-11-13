/* (c) https://github.com/MontiCore/monticore */

package dbPerformance;

import umlp.backendrte.common.DatabaseAccessManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BulkRequestPreparedStatementDao implements ExpDao{
  public PreparedStatement p;

  public String getStatement(){
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < ExpConstants.NUM_TABLES; i++) {
        builder.append(String.format("SELECT * from %s WHERE id = ? ", "t" + i));
        if (i != ExpConstants.NUM_TABLES - 1) {
          builder.append(" UNION ");
        }
      }

      return builder.toString();
  }

  @Override
  public boolean dbContainsId(String id) throws SQLException {
    if(p == null){
      p = DatabaseAccessManager.prepareStatement(getStatement());
    }

    p.clearParameters();

    for (int i = 0; i < ExpConstants.NUM_TABLES; i++) {
      p.setString(i + 1, id);
    }

    Optional<ResultSet> res = Optional.ofNullable(p.executeQuery());
    // TODO: does not work with parameters!
    if(res.isEmpty()){
      return false;
    }else {
      ResultSet rs = res.get();
      return rs.next();
    }
  }
}
