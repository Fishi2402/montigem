<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public Optional<${typeName}> select${typeName}(long gemId)")}
  ensureSelect${typeName}Created();
  synchronized(select${typeName}){
    try{
      select${typeName}.clearParameters();
      select${typeName}.setLong(1, gemId);
      try(ResultSet res = select${typeName}.executeQuery()) {
        if(res.next()){
          return unpack${typeName}(res);
        }else{
          return Optional.empty();
        }
      } catch (SQLException e){
        throw new RuntimeException(e);
      }
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }
