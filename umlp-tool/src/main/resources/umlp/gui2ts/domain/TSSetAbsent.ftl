<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public void set${name?cap_first}Absent(NotificationScope scope)")}

{
  this._internal_set${name?cap_first}(-1, scope);
  this.set${name?cap_first}Present(false);
  this.check${name?cap_first}();
}
