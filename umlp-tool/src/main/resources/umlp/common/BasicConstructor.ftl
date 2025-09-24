<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "attributes")}
<#macro basicConstructorParameters attributes>
  <#list attributes as attr>
    ${attr.getMCType().printType()} ${attr.name}<#if !attr?is_last>,</#if>
  </#list>
</#macro>
<#assign x><@basicConstructorParameters attributes/></#assign>
${cd4c.constructor("public ${initializationTypeName}(${x})")}
<#list attributes as attr>
  this.${attr.name} = ${attr.name};
</#list>
