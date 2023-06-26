<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName","typeName", "attributes")}
${cd4c.method("public void ensureSelectAll${typeName}IdsCreated()")}
  if(selectAllIds${typeName} == null) {
    selectAllIds${typeName} = DatabaseAccessManager.prepareStatement("SELECT id FROM ${tableName};");
  }
