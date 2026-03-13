<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}
${cd4c.method("public ${type} create${name?cap_first}()")}
  return instances.get${name?cap_first}();
