<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName","attr", "innerType", "dbName")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public void update${attrName}(long gemId, List<${innerType}> ${attrName?uncap_first})")}

  if(remove${attrName}Statement == null) {
    remove${attrName}Statement = "DELETE FROM ${tableName} WHERE id = ?";
  }
  if(update${attrName}Statement == null) {
    update${attrName}Statement = "INSERT INTO ${tableName} (id, _value, index) VALUES (?, ?, ?)";
  }
  try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st1 = connection.prepareStatement(remove${attrName}Statement);
      PreparedStatement st2 = connection.prepareStatement(update${attrName}Statement);){

    connection.setAutoCommit(false);
    st1.clearParameters();
    st1.setLong(1, gemId);
    st1.executeUpdate();

    long index = 0;
    for(${innerType} element : ${attrName?uncap_first}) {
      st2.clearParameters();
      st2.setLong(1, gemId);
      ${includeArgs("umlp.dao.Set", "st2", 2, dbName, "element", false,"")};
      st2.setLong(3, index++);
      st2.addBatch();
    }
    st2.executeBatch();
    connection.commit();
    connection.setAutoCommit(true);
  } catch (SQLException e){
    throw new RuntimeException(e);
  }
