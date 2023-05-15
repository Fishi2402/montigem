<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public boolean containsObserver(${name}Observer o)")}

return this.${name?uncap_first}Observers.includes(o);