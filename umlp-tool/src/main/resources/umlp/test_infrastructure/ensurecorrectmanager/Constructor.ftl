<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "attributes")}
${cd4c.constructor("public ${className}(${cdName}Manager manager)")}
<#list attributes as attr>
  this.${attr.name} = new ${attr.getMCType().printType()}(manager);
</#list>
