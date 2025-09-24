<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "attributeName")}
${cd4c.method("public ${initializationTypeName} ${attributeName}GemListIds(List<Long> ids)")}
  this._${attributeName}._internal_add(this._${attributeName}.size(), ids);
  return this;
