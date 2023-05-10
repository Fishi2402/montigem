<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public boolean contains${name?cap_first}(${targetName} o)")}
return this._${name}.includes(o.getGemId());
