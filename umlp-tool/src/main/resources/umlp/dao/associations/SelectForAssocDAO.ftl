<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oneSideName", "otherSide")}
<#assign methodName = otherSideName?cap_first+"IdsFor"+oneSideName?cap_first>
${cd4c.method("public List<Long> select${methodName}(long ${oneSideName}GemId)")}
  ensureSelect${methodName?cap_first}Created();
  try {
    select${methodName}.clearParameters();
    select${methodName}.setLong(1, ${oneSideName}GemId);
    List<Long> res = new ArrayList<>();
    ResultSet rs = select${methodName}.executeQuery();
    while(rs.next()){
      res.add(rs.getLong(1));
    }
    return res;
  }catch (SQLException e){
    throw new RuntimeException(e);
  }
