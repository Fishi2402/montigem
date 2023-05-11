<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public void removeObserver(${name}Observer o)")}

const index = this.${name?uncap_first}Observers.indexOf(o);
if (index !== -1) {
  this.${name?uncap_first}Observers.splice(index, 1);
}
