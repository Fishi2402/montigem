<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName","attr", "innerType", "dbName")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public void update${attrName}(long gemId, List<${innerType}> ${attrName?uncap_first})")}

  if(update${attrName?cap_first}Statement == null) {
    update${attrName?cap_first}Statement = DatabaseAccessManager.prepareStatement("match(n:${typeName} {gemId: ?}) SET n.${attrName} = ?");
  }
  try{
    update${attrName?cap_first}Statement.clearParameters();
    update${attrName?cap_first}Statement.setLong(1, gemId);
    update${attrName?cap_first}Statement.setObject(2, ${attrName?uncap_first});
    update${attrName?cap_first}Statement.executeUpdate();

  } catch (SQLException e){
    throw new RuntimeException(e);
  }
