<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "attr", "innerType", "dbType")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public List<${innerType}> select${attrName}(long gemId)")}
  if(select${attrName}Statement == null) {
    select${attrName}Statement = DatabaseAccessManager.prepareStatement("SELECT _value FROM ${tableName} WHERE id = ?");
  }
  try {
  select${attrName}Statement.clearParameters();
  select${attrName}Statement.setLong(1, gemId);
  List<${innerType}> res = new ArrayList<>();
    ResultSet rs = select${attrName}Statement.executeQuery();
    while(rs.next()){
      res.add(${includeArgs("umlp.dao.Get", "rs", attr, 1)});
    }
    return res;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
