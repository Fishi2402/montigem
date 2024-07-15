<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder")}
${cd4c.method("public ${builder} ${name}(List<${type}> o)")}

this._${name}.clear();
this._${name}.addAll(o);
return this.realBuilder;
