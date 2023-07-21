package dbPerformance;

import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MultipleRequestsDao implements ExpDao{
  @Override
  public boolean dbContainsId(String id) throws SQLException {
    for(int i = 0; i < ExpConstants.NUM_TABLES; i++) {
      Optional<ResultSet> res = DatabaseAccessManager4SQL.executeSQLQuery(String.format("SELECT * from %s WHERE id = '%s'", "t" + i, id));
      if(res.isPresent() && res.get().next()){
        return true;
      }
    }

    return false;
  }
}
