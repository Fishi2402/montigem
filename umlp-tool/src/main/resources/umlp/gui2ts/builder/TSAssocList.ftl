<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder")}
${cd4c.method("public ${builder} ${name}(Set<${type}> o)")}

super.clear${name?cap_first}s();
super.addAll${name?cap_first}s2([...o]);
this.${name}Present = true;
return this;
