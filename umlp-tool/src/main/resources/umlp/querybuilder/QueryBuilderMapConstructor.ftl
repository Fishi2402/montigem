<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mappedDomainClassList", "initializationTypeName")}
${cd4c.constructor("public ${initializationTypeName}()")}
  // Generated
<#list mappedDomainClassList as domainClass>
  map.put("${domainClass}", new ${domainClass}QueryBuilder());
</#list>
