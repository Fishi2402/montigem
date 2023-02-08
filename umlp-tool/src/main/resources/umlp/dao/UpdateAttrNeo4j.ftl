<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "attributeName", "attributeType")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}
  if(update${attributeName?cap_first}Statement == null) {
    update${attributeName?cap_first}Statement = DatabaseAccessManager.prepareStatement("match(n:${typeName} {gemId: ?}) SET n.${attributeName} = ?");
  }
  try{
    update${attributeName?cap_first}Statement.clearParameters();
    update${attributeName?cap_first}Statement.setLong(1, gemId);
    update${attributeName?cap_first}Statement.set${attributeType?cap_first}(2, ${attributeName});
    update${attributeName?cap_first}Statement.execute();
  } catch (SQLException e){
    throw new RuntimeException(e);
  }