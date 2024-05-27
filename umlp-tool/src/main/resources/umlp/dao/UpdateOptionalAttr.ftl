<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "attributeName", "dbAttrName", "attributeType", "innerType")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}
  <#assign statement="update"+attributeName?cap_first+"Statement">
  if(${statement} == null) {
    ${statement} = DatabaseAccessManager.prepareStatement("UPDATE ${tableName} SET ${dbAttrName} = ? WHERE id = ?");
  }
  synchronized(${statement}){
    try{
      ${statement}.clearParameters();
      ${includeArgs("umlp.dao.Set", statement, 1, innerType, attributeName, true,"")};
      ${statement}.setLong(2, gemId);
      ${statement}.executeUpdate();
    } catch (SQLException e){
      throw new RuntimeException(e);
    }
  }
