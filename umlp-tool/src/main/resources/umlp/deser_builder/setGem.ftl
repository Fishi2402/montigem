<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "attributeType", "attributeName")}
${cd4c.method("public ${initializationTypeName} ${attributeName}Gem(${attributeType} gem)")}
  this._${attributeName} = gem;
  return this;
