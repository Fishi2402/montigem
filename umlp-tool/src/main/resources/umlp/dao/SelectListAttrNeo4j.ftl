<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "tableName", "attr", "innerType", "dbType")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public List<${innerType}> select${attrName}(long gemId)")}
  if(select${attrName}Statement == null) {
    select${attrName}Statement = "MATCH(n:${typeName} {gemId: $p1}) UNWIND n.${attrName} AS ${attrName} RETURN ${attrName}";
  }
  try(Session session = DatabaseAccessManager4Neo4j.getSession()) {
    Result rs = session.run(select${attrName}Statement,
      Values.parameters("p1", gemId));
    List<${innerType}> res = new ArrayList<>();
    while(rs.hasNext()){
      org.neo4j.driver.Record rd = rs.next();
      res.add(${includeArgs("umlp.dao.GetNeo4j", "rd", attr, 0,"_Neo4j")});
    }
    return res;
  }
