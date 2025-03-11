package loadingtests.ocl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryBuilderMap {
  protected Map<String, QueryBuilder> map = new HashMap<>();
  public QueryBuilderMap() {
    map.put("Person", new PersonQueryBuilder());
  }

  public Optional<QueryBuilder> getBuilder(String clazz) {
    return Optional.ofNullable(map.get(clazz));
  }

}
