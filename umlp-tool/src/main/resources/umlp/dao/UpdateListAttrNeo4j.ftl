<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "tableName","attr", "innerType", "dbName")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public void update${attrName}(long gemId, List<${innerType}> ${attrName?uncap_first})")}

  if(remove${attrName}Statement == null) {
    remove${attrName}Statement = "MATCH(n:${typeName} {gemId: $p1}) SET n.${attrName} = []";
  }
  if(update${attrName}Statement == null) {
    update${attrName}Statement = "MATCH(n:${typeName} {gemId: $p1}) SET n.${attrName} = n.${attrName} + $p2";
  }
  DatabaseAccessManager4Neo4j.executeWrite(remove${attrName}Statement, Values.parameters("p1", gemId));

  long index = 0;
  for(${innerType} element : ${attrName?uncap_first}) {
    DatabaseAccessManager4Neo4j.executeWrite(update${attrName}Statement,
      Values.parameters("p1", gemId,
      ${includeArgs("umlp.dao.SetNeo4j", "st", "p2", dbName, "element", false,"_Neo4j")}
    ));
    // TODO there is no index currently -> need to use neo4j map? update${attrName}Statement.setLong(3, index++);
  }

