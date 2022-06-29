<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder")}
${cd4c.method("public ${builder} ${name}(${type} ${name})")}

this.${name} = ${name};
is${name?cap_first}Present = true;
return this;



