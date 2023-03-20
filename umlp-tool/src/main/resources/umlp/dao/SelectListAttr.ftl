<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName","className","attrName", "innerType", "dbType")}
${cd4c.method("public List<${innerType}> select${attrName?cap_first}(long gemId)")}
  if(select${attrName?cap_first}Statement == null) {
    select${attrName?cap_first}Statement = DatabaseAccessManager.prepareStatement("SELECT _value FROM ${tableName} WHERE id = ?");
  }
  try {
  select${attrName?cap_first}Statement.clearParameters();
  select${attrName?cap_first}Statement.setLong(1, gemId);
  List<${innerType}> res = new ArrayList<>();
    ResultSet rs = select${attrName?cap_first}Statement.executeQuery();
    while(rs.next()){
      res.add(rs.get${dbType}(1));
    }
    return res;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }