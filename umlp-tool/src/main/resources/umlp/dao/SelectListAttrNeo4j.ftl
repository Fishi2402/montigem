<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "tableName", "attr", "innerType", "dbType")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public List<${innerType}> select${attrName}(long gemId)")}
  if(select${attrName}Statement == null) {
    select${attrName}Statement = DatabaseAccessManager.prepareStatement("match(n:${typeName} {gemId: ?}) UNWIND n.${attrName} AS ${attrName} return ${attrName}");
  }
  try {
  select${attrName}Statement.clearParameters();
  select${attrName}Statement.setLong(1, gemId);
  List<${innerType}> res = new ArrayList<>();
    ResultSet rs = select${attrName}Statement.executeQuery();
    while(rs.next()){
      res.add(${includeArgs("umlp.dao.Get", "rs", attr, 1,"_Neo4j")});
    }
    return res;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
