<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributeName")}
${cd4c.method("public ${className} ${attributeName}GemListIds(List<Long> ids)")}
  this._${attributeName}._internal_add(this._${attributeName}.size(), ids);
  return this;
