<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "attributeName", "attributeType", "dbType", "isOptional")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}

  update${attributeName?cap_first}Statement = "MATCH(n:${typeName} {gemId: $p1}) SET n.${attributeName} = $p2";

  DatabaseAccessManager4Neo4j.executeWrite(
    update${attributeName?cap_first}Statement,
    Values.parameters(
      "p1", gemId,
       ${includeArgs("umlp.dao.SetNeo4j", statement, "p2", dbType, attributeName, isOptional,"_Neo4j")}
    )
  );
