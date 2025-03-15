<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName")}
<#assign query = "selectFiltered" + typeName + "List">
${cd4c.method("public List<${typeName}> selectFiltered${typeName}List(String filter)")}
  ensureSelectFiltered${typeName}Created(filter);
  List<${typeName}> result = new ArrayList<>();

  synchronized (${query}) {
    try {
      ${query}.clearParameters();
      try (ResultSet res = ${query}.executeQuery()) {
        while (res.next()) {
          Optional<${typeName}> obj = unpack${typeName}(res);
          if (obj.isPresent()) {
            result.add(obj.get());
          }
          else {
            throw new RuntimeException("0xBE000 Failed Unpacking ${typeName} object with id "
              + res.getLong(1));
          }
        }
        if(!res.isClosed()) {
          res.close();
        }
        return result;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }