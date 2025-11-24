<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public Optional<${typeName}> select${typeName}(long gemId)")}
  ensureSelect${typeName}Created();

    try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(select${typeName}); ){
      st.clearParameters();
      st.setLong(1, gemId);
      try(ResultSet res = st.executeQuery()) {
        if(res.next()){
          Optional<${typeName}> obj = unpack${typeName}(res);
          if(!res.isClosed()) {
            res.close();
          }
          return obj;
        }else{
          return Optional.empty();
        }
      } catch (SQLException e){
        throw new RuntimeException(e);
      }
    }catch (SQLException e){
      throw new RuntimeException(e);
    }

