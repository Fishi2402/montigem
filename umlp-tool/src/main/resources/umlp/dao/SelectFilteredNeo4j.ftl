<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName")}
<#assign query = "selectFiltered" + typeName + "List">
${cd4c.method("public List<Long> selectFiltered${typeName}List(String filter)")}
  ensureSelectFiltered${typeName}Created(filter);
  List<Long> result = new ArrayList<>();

  try(Session session = DatabaseAccessManager4Neo4j.getSession()){
    Result res = session.run(
      ${query}
    );
    while(res.hasNext()){
      result.add(res.next().get(1).asLong());
    }
  }
  return result;
