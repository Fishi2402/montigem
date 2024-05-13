<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder")}
${cd4c.method("public ${builder} ${name}Add(${type} o)")}

this._${name}.add(o);
return (${builder}) this;

