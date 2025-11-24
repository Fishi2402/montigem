<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName")}
<#assign query = "selectFiltered" + typeName + "List">
${cd4c.method("public void ensureSelectFiltered${typeName}Created(String filter)")}
  try {
    ${query} = OCL2SQL.getInstance().convert(filter);
  } catch(Exception e) {
    throw new RuntimeException(e);
  }

