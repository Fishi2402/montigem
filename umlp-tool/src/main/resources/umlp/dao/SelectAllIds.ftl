<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public List<Long> selectAllIds${typeName}()")}
  ensureSelectAll${typeName}IdsCreated();
  List<Long> result = new ArrayList<>();

  try{
    selectAllIds${typeName}.clearParameters();
    ResultSet res = selectAllIds${typeName}.executeQuery();
    while(res.next()){
      result.add(res.getLong(1));
    }
  return result;
  }catch (SQLException e){
    throw new RuntimeException(e);
  }
