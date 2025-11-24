<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "attributeName", "dbAttrName", "attributeType", "dbType")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}
  if(update${attributeName?cap_first}Statement == null) {
    update${attributeName?cap_first}Statement = "UPDATE ${tableName} SET ${dbAttrName} = ? WHERE id = ?";
  }
//${tableName}

    try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(update${attributeName?cap_first}Statement);){
      <#assign statement="st">
      st.clearParameters();
      ${includeArgs("umlp.dao.Set", statement, 1, dbType, attributeName, false,"")};
      st.setLong(2, gemId);
      st.executeUpdate();

    } catch (SQLException e){
      throw new RuntimeException(e);
    }

