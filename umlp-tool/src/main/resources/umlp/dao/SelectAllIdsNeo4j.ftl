<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public List<Long> selectAllIds${typeName}()")}
  ensureSelectAll${typeName}IdsCreated();
  List<Long> result = new ArrayList<>();

  try(Session session = DatabaseAccessManager4Neo4j.getSession()){
    Result res = session.run(
      selectAllIds${typeName}
    );
    while(res.hasNext()){
      result.add(res.next().get(0).asLong());
    }
  }
  return result;

