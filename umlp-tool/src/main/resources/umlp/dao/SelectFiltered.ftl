<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName")}
<#assign query = "selectFiltered" + typeName + "List">
${cd4c.method("public List<Long> selectFiltered${typeName}List(String filter)")}
  ensureSelectFiltered${typeName}Created(filter);
  List<Long> result = new ArrayList<>();

  synchronized (${query}) {
    try {
      ${query}.clearParameters();
      try (ResultSet res = ${query}.executeQuery()) {
        while (res.next()) {
          result.add(res.getLong(1));
        }
        return result;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
