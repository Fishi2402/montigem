<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("startingSide", "endingSide")}
<#assign methodName = endingSide?cap_first+"IdFor"+startingSide?cap_first>
${cd4c.method("public Optional<Long> select${methodName}(long ${startingSide}GemId)")}
  ensureSelect${methodName?cap_first}Created();
  try {
    select${methodName}.clearParameters();
    select${methodName}.setLong(1, ${startingSide}GemId);
    Optional<Long> res = Optional.empty();
    ResultSet rs = select${methodName}.executeQuery();
    if(rs.next()){
      res = Optional.of(rs.getLong(1));
    }
    return res;
  }catch (SQLException e){
    throw new RuntimeException(e);
  }
