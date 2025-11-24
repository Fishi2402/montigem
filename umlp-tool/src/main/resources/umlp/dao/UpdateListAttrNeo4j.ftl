<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "tableName","attr", "innerType", "dbName")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public void update${attrName}(long gemId, List<${innerType}> ${attrName?uncap_first})")}

  if(remove${attrName}Statement == null) {
    remove${attrName}Statement = "MATCH(n:${typeName} {gemId: $1}) SET n.${attrName} = []";
  }
  if(update${attrName}Statement == null) {
    update${attrName}Statement = "MATCH(n:${typeName} {gemId: $1}) SET n.${attrName} = n.${attrName} + $2";
  }
  try(Connection connection = DatabaseAccessManager.getConnection();
    PreparedStatement removeSt = connection.prepareStatement(remove${attrName?cap_first}Statement);
    PreparedStatement st = connection.prepareStatement(update${attrName?cap_first}Statement);){
    removeSt.clearParameters();
    removeSt.setLong(1, gemId);
    removeSt.executeUpdate();

    long index = 0;
    for(${innerType} element : ${attrName?uncap_first}) {
      st.clearParameters();
      st.setLong(1, gemId);
      ${includeArgs("umlp.dao.Set", "st", 2, dbName, "element", false,"_Neo4j")};
      // TODO there is no index currently -> need to use neo4j map? update${attrName}Statement.setLong(3, index++);
      st.executeUpdate();
    }

  } catch (SQLException e){
    throw new RuntimeException(e);
  }
