<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public ${targetName} remove${name?cap_first}(int idx, NotificationScope scope)")}
${targetName} c = _${name}.get(idx);
_internal_remove${name?cap_first}(Collections.singleton(idx));
return c;
