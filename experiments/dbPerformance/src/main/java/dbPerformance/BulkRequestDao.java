package dbPerformance;

import com.google.common.base.Stopwatch;
import umlp.backendrte.common.DatabaseAccessManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BulkRequestDao implements ExpDao{
  public Stopwatch stopwatch = Stopwatch.createUnstarted();

  public String getStatement(String id) throws SQLException {
    stopwatch.start();
    try {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < ExpConstants.NUM_TABLES; i++) {
        builder.append(String.format("SELECT * from %s WHERE id = '%s' ", "t" + i, id));
        if (i != ExpConstants.NUM_TABLES - 1) {
          builder.append(" UNION ");
        }
      }

      return builder.toString();
    }finally {
      stopwatch.stop();
    }
  }

  @Override
  public boolean dbContainsId(String id) throws SQLException {
    Optional<ResultSet> res = DatabaseAccessManager.executeSQLQuery(getStatement(id));
    // TODO: does not work with parameters!
    if(res.isEmpty()){
      return false;
    }else {
      ResultSet rs = res.get();
      return rs.next();
    }
  }
}
