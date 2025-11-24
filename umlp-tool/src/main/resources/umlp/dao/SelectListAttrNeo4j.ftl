<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "tableName", "attr", "innerType", "dbType")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public List<${innerType}> select${attrName}(long gemId)")}
  if(select${attrName}Statement == null) {
    select${attrName}Statement = "MATCH(n:${typeName} {gemId: $1}) UNWIND n.${attrName} AS ${attrName} RETURN ${attrName}";
  }
  try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(select${attrName}Statement);) {
  st.clearParameters();
  st.setLong(1, gemId);
  List<${innerType}> res = new ArrayList<>();
    try(ResultSet rs = st.executeQuery()) {
      while(rs.next()){
      res.add(${includeArgs("umlp.dao.Get", "rs", attr, 1,"_Neo4j")});
    }
      return res;
    } catch(SQLException e) {
      throw new RuntimeException(e);
    }
  } catch (SQLException e) {
    throw new RuntimeException(e);
  }
