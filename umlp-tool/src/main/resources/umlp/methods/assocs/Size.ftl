<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public int size${name?cap_first}s()")}
return this._internal_get${name?cap_first}GemList().size();
