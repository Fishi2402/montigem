<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public void set${name?cap_first}Absent(NotificationScope scope)")}

{
  _internal_set${name?cap_first}(-1, scope);
  set${name?cap_first}Present(false);
  check${name?cap_first}();
}
