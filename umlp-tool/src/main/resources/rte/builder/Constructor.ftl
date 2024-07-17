<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "assocToManagerMap", "initialAttributes")}
${cd4c.constructor("public ${name}Builder()")}

this.realBuilder = (${name}Builder) this;
<#list initialAttributes as i>
  this.${i[0]}(${i[1]});
</#list>
