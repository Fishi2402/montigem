<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public GemList<${targetName}> get${name?cap_first}()")}

return this._${name};