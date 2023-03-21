<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "attributeName", "attributeType", "innerType")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}
  if(update${attributeName?cap_first}Statement == null) {
    update${attributeName?cap_first}Statement = DatabaseAccessManager.prepareStatement("UPDATE ${tableName} SET \"${attributeName}\" = ? WHERE id = ?");
  }
  try{
    update${attributeName?cap_first}Statement.clearParameters();
    ${includeArgs("umlp.dao.set." + innerType?replace(".", "_"), "update${attributeName?cap_first}Statement", innerType?cap_first, attributeName, 1, true)}
    update${attributeName?cap_first}Statement.setLong(2, gemId);
    update${attributeName?cap_first}Statement.execute();
  } catch (SQLException e){
    throw new RuntimeException(e);
}