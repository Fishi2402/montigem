<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public boolean containsAll${name?cap_first}(Collection<${targetName}> o)")}
return o.every(e => this._${name}.includes(e));
