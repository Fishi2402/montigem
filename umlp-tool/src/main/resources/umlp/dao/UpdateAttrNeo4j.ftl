<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "attributeName", "attributeType", "dbType", "isOptional")}
${cd4c.method("public void update${attributeName?cap_first}(long gemId, ${attributeType} ${attributeName})")}

  update${attributeName?cap_first}Statement = "MATCH(n:${typeName} {gemId: $1}) SET n.${attributeName} = $2";

  try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(update${attributeName?cap_first}Statement);){
    <#assign statement="st">
    st.clearParameters();
    st.setLong(1, gemId);
    ${includeArgs("umlp.dao.Set", statement, 2, dbType, attributeName, isOptional,"_Neo4j")};
    st.executeUpdate();

  } catch (SQLException e){
    throw new RuntimeException(e);
  }
