<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "attributeName", "attributeType", "dbType")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}
  if(update${attributeName?cap_first}Statement == null) {
    update${attributeName?cap_first}Statement = DatabaseAccessManager.prepareStatement("UPDATE ${tableName} SET \"${attributeName}\" = ? WHERE id = ?");
  }
//${tableName}
  try{
    <#assign statement="update"+attributeName?cap_first+"Statement">
    ${statement}.clearParameters();
    ${includeArgs("umlp.dao.Set", statement, 1, dbType, attributeName, false)};
    ${statement}.setLong(2, gemId);
    ${statement}.execute();
  } catch (SQLException e){
    throw new RuntimeException(e);
  }