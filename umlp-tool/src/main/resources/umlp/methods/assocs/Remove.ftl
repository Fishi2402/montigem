<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public boolean remove${name?cap_first}(${targetName} o, NotificationScope scope)")}
if (this.${name}.contains(o)) {
  this._internal_remove${name?cap_first}(java.util.Collections.singleton(this.${name}.indexOf(o)), scope);
  return true;
}

return false;