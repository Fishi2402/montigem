<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className","attrName", "innerType")}
${cd4c.method("public List<${innerType}> select${attrName?cap_first}(long gemId)")}
  if(select${attrName?cap_first}Statement == null) {
    select${attrName?cap_first}Statement = DatabaseAccessManager.prepareStatement("SELECT value FROM ${className?uncap_first}$${attrName} WHERE id = ?");
  }
  try {
  select${attrName?cap_first}Statement.clearParameters();
  select${attrName?cap_first}Statement.setLong(1, gemId);
  List<${innerType}> res = new ArrayList<>();
    ResultSet rs = select${attrName?cap_first}Statement.executeQuery();
    if(rs.next()){
      res.add(rs.get${innerType?cap_first}(1));
    }
    return res;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }