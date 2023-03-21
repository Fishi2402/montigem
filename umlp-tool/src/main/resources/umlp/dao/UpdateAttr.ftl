<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "attributeName", "attributeType")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}
  if(update${attributeName?cap_first}Statement == null) {
    update${attributeName?cap_first}Statement = DatabaseAccessManager.prepareStatement("UPDATE ${tableName} SET \"${attributeName}\" = ? WHERE id = ?");
  }
//${tableName}
  try{
    update${attributeName?cap_first}Statement.clearParameters();
    ${includeArgs("umlp.dao.set." + attributeType?replace(".", "_"), "update${attributeName?cap_first}Statement", attributeType, attributeName, 1, false)}
    update${attributeName?cap_first}Statement.setLong(2, gemId);
    update${attributeName?cap_first}Statement.execute();
  } catch (SQLException e){
    throw new RuntimeException(e);
  }