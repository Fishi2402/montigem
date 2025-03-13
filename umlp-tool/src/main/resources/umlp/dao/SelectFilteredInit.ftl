<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName")}
${cd4c.method("public void ensureSelectFiltered${typeName}Created(String filter)")}
  if (selectFiltered${typeName}List == null) {
    try {
      String query = OCL2SQL.getInstance().convert(filter);
      selectAllIds${typeName} = DatabaseAccessManager.prepareStatement(query);
    } catch (IOException | OCLException e) {
      throw new RuntimeException(e);
    }
  }
