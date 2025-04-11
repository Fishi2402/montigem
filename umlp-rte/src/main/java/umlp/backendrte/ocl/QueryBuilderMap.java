package umlp.backendrte.ocl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class QueryBuilderMap {
  protected Map<String, QueryBuilder> map = new HashMap<>();

  protected static QueryBuilderMap instance;

  protected static QueryBuilderMap getInstance() {
    return Objects.requireNonNull(QueryBuilderMap.instance, "QueryBuilderMap is not initialized!");
  }

  public Optional<QueryBuilder> getBuilder(String clazz) {
    return Optional.ofNullable(map.get(clazz));
  }

}
