<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "initializationTypeName", "attributes")}
${cd4c.constructor("public ${initializationTypeName}(${cdName}Manager manager)")}
<#list attributes as attr>
  this.${attr.name} = new ${attr.getMCType().printType()}(manager);
</#list>
