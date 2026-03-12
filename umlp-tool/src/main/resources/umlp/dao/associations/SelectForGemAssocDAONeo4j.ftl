<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("startingSide", "endingSide")}
<#assign methodName = endingSide?cap_first+"IdFor"+startingSide?cap_first>
${cd4c.method("public Optional<Long> select${methodName}(long ${startingSide}GemId)")}
  ensureSelect${methodName?cap_first}Created();
try(Session session = DatabaseAccessManager4Neo4j.getSession()) {
  Result rs = session.run(select${methodName}, Values.parameters("p1", ${startingSide}GemId));
  Optional<Long> res = Optional.empty();
  if(rs.hasNext()){
      res = Optional.of(rs.next().get(0).asLong());
  }
  return res;
}
