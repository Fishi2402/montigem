<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "attributeName", "dbAttrName", "attributeType", "innerType")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}
  <#assign statement="update"+attributeName?cap_first+"Statement">
  if(${statement} == null) {
    ${statement} = "UPDATE ${tableName} SET ${dbAttrName} = ? WHERE id = ?";
  }

    try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(${statement})){
      st.clearParameters();
      ${includeArgs("umlp.dao.Set", "st", 1, innerType, attributeName, true,"")};
      st.setLong(2, gemId);
      st.executeUpdate();
    } catch (SQLException e){
      throw new RuntimeException(e);
    }

