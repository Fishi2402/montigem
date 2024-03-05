<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "attributeName", "attributeType", "dbType", "isOptional")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}
  if(update${attributeName?cap_first}Statement == null) {
    update${attributeName?cap_first}Statement = DatabaseAccessManager.prepareStatement("match(n:${typeName} {gemId: ?}) SET n.${attributeName} = ?");
  }
  try{
    <#assign statement="update"+attributeName?cap_first+"Statement">
    ${statement}.clearParameters();
    ${statement}.setLong(1, gemId);
    ${includeArgs("umlp.dao.Set", statement, 2, dbType, attributeName, isOptional,"_Neo4j")};
    ${statement}.executeUpdate();

  } catch (SQLException e){
    throw new RuntimeException(e);
  }
