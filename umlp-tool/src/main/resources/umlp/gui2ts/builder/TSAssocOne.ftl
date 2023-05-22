<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder")}
${cd4c.method("public ${builder} ${name}(${type} ${name})")}

this._${name} = new Gem<${type}>().Gem2(${name});
this.${name}Present = true;
return this;