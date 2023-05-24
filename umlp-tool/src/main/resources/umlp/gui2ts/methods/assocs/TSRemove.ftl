<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public boolean remove${name?cap_first}(${targetName} o, NotificationScope scope)")}
if (this._${name}.contains(o.getGemId())) {
  this._internal_remove${name?cap_first}(new Set([this._${name}.indexOfId(o.getGemId())]), scope);
  return true;
}

return false;
