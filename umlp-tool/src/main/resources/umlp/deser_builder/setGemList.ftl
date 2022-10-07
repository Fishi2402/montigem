<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributeType", "attributeName")}
${cd4c.method("public ${className} ${attributeName}GemList(${attributeType} gemList)")}
  this.${attributeName}Present = gemList != null;
  this.${attributeName} = gemList;
  return this;