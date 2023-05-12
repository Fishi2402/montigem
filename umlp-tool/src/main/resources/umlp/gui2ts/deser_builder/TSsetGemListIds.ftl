<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributeName")}
${cd4c.method("public ${className} ${attributeName}GemListIds(List<Long> ids)")}
  this.${attributeName}Present = true;
  this._${attributeName}._internal_add(this._${attributeName}.length, ids);
  return this;