<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "className","attrName", "innerType")}
${cd4c.method("public void update${attrName?cap_first}(long gemId, List<${innerType}> ${attrName})")}

  if(remove${attrName?cap_first}Statement == null) {
    remove${attrName?cap_first}Statement = DatabaseAccessManager.prepareStatement("DELETE FROM ${tableName} WHERE id = ?");
  }
  if(update${attrName?cap_first}Statement == null) {
    update${attrName?cap_first}Statement = DatabaseAccessManager.prepareStatement("INSERT INTO ${tableName} (id, _value) VALUES (?, ?)");
  }
  try{
    remove${attrName?cap_first}Statement.clearParameters();
    remove${attrName?cap_first}Statement.setLong(1, gemId);
    remove${attrName?cap_first}Statement.execute();

    for(${innerType} element : ${attrName}) {
      update${attrName?cap_first}Statement.clearParameters();
      update${attrName?cap_first}Statement.setLong(1, gemId);
      ${includeArgs("umlp.dao.set." + innerType?replace(".", "_"), "update${attrName?cap_first}Statement" ,innerType, "element", 2, false)}
      update${attrName?cap_first}Statement.execute();
    }

  } catch (SQLException e){
    throw new RuntimeException(e);
  }