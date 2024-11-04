<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public ${targetName} remove${name?cap_first}1(int idx, NotificationScope scope)")}
const c: ${targetName} = this._${name}[idx];
this._internal_remove${name?cap_first}(new Set([idx]), scope);
return c;
