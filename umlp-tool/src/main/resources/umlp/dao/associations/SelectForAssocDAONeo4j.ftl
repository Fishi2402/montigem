<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oneSideName", "otherSide")}
<#assign methodName = otherSide?cap_first+"IdsFor"+oneSideName?cap_first>
${cd4c.method("public List<Long> select${methodName}(long ${oneSideName}GemId)")}
ensureSelect${methodName?cap_first}Created();

try(Session session = DatabaseAccessManager4Neo4j.getSession()) {
  Result rs = session.run(select${methodName}, Values.parameters("p1", ${oneSideName}GemId));
  List<Long> res = new ArrayList<>();

  while(rs.hasNext()){
    res.add(rs.next().get(0).asLong());
  }
  return res;
}

