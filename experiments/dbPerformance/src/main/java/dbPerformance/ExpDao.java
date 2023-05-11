package dbPerformance;

import java.sql.SQLException;

public interface ExpDao {
  boolean dbContainsId(String i) throws SQLException;
}
