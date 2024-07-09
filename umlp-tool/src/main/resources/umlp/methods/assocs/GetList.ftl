<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public List<${targetName}> get${name?cap_first}List()")}

return this._internal_get${name?cap_first}GemList();
