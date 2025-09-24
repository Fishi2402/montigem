<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "attributeType", "attributeName")}
${cd4c.method("public ${initializationTypeName} ${attributeName}(${attributeType} value)")}
  super.${attributeName}(value);
  return this;
