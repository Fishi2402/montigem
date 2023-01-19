<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void add${name?cap_first}(${targetName} o, NotificationScope scope)")}

add${name?cap_first}(_${name}.size(), o, scope);