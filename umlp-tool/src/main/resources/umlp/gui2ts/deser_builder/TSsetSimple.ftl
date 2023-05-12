<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributeType", "attributeName")}
${cd4c.method("public ${className} ${attributeName}(${attributeType} value)")}
  super.${attributeName}(value);
  return this;