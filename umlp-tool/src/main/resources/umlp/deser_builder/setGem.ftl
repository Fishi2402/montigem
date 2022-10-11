<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributeType", "attributeName")}
${cd4c.method("public ${className} ${attributeName}Gem(${attributeType} gem)")}
  this.${attributeName}Present = gem != null;
  this.${attributeName} = gem;
  return this;