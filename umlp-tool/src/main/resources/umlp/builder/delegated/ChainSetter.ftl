<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delType", "chain")}
${cd4c.method("public ${chain} ${name}(${delType} v)")}

this.delegate.${name}(v);
return this;
