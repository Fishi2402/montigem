package dbPerformance;

import umlp.backendrte.common.DatabaseAccessManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncodedInIdDao implements ExpDao{
  Pattern tablePattern = Pattern.compile("^([^-]*)-");
  @Override
  public boolean dbContainsId(String i) throws SQLException {
    Matcher m = tablePattern.matcher(i);
    if(!m.find()){
      return false;
    }

    String table = m.group(1);
    Optional<ResultSet> res = DatabaseAccessManager.executeSQLQuery(String.format("Select * from %s Where id = '%s'", table, i));
    if(res.isEmpty()){
      return false;
    }

    return res.get().next();
  }
}
