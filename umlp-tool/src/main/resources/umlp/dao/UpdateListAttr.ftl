<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName","attr", "innerType", "dbName")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public void update${attrName}(long gemId, List<${innerType}> ${attrName?uncap_first})")}

  if(remove${attrName}Statement == null) {
    remove${attrName}Statement = DatabaseAccessManager.prepareStatement("DELETE FROM ${tableName} WHERE id = ?");
  }
  if(update${attrName}Statement == null) {
    update${attrName}Statement = DatabaseAccessManager.prepareStatement("INSERT INTO ${tableName} (id, _value, index) VALUES (?, ?, ?)");
  }
  try{
    remove${attrName}Statement.clearParameters();
    remove${attrName}Statement.setLong(1, gemId);
    remove${attrName}Statement.executeUpdate();

    long index = 0;
    for(${innerType} element : ${attrName?uncap_first}) {
      update${attrName}Statement.clearParameters();
      update${attrName}Statement.setLong(1, gemId);
      ${includeArgs("umlp.dao.Set", "update"+attrName+"Statement", 2, dbName, "element", false,"")};
      update${attrName}Statement.setLong(3, index++);
      update${attrName}Statement.executeUpdate();
    }

  } catch (SQLException e){
    throw new RuntimeException(e);
  }
