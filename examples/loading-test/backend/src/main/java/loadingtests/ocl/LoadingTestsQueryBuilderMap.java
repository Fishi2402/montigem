package loadingtests.ocl;

import umlp.backendrte.ocl.QueryBuilder;
import umlp.backendrte.ocl.QueryBuilderMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoadingTestsQueryBuilderMap extends QueryBuilderMap {
  protected Map<String, QueryBuilder> map = new HashMap<>();
  public LoadingTestsQueryBuilderMap() {
    map.put("Person", new PersonQueryBuilder());
  }

  public static void init() {
    instance = new LoadingTestsQueryBuilderMap();
  }

  public Optional<QueryBuilder> getBuilder(String clazz) {
    return Optional.ofNullable(map.get(clazz));
  }

}
