<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public boolean containsAll${name?cap_first}(GemList<${targetName}> o)")}
return this._internal_get${name?cap_first}GemList().containsAll(o);
