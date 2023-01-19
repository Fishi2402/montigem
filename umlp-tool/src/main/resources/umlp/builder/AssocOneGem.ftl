<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder")}
${cd4c.method("public ${builder} ${name}Gem(Gem<${type}> ${name})")}

this._${name} = ${name};
${name}Present = ${name} != null;
return this;