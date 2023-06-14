<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public List<${typeName}> selectAll${typeName}()")}
  ensureSelectAll${typeName}Created();
  List<${typeName}> result = new ArrayList<>();

  try{
    select${typeName}.clearParameters();
    ResultSet res = select${typeName}.executeQuery();
    while(res.next()){
      Optional<${typeName}> tmp = unpack${typeName}(res);
      if(tmp.isPresent()){
        result.add(tmp.get());
      }
    }
  return result;
  }catch (SQLException e){
    throw new RuntimeException(e);
  }
