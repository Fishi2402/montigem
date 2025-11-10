<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName")}
<#assign query = "selectFiltered" + typeName + "List">
${cd4c.method("public void ensureSelectFiltered${typeName}Created(String filter)")}
  try {
    String query = OCL2SQL.getInstance().convert(filter);
    ${query} = DatabaseAccessManager.prepareStatement(query);
  } catch (IOException | OCLException e) {
    throw new RuntimeException(e);
  }
