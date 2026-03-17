<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mappedDomainClassList", "className")}
${cd4c.constructor("public ${className}()")}
  // Generated
<#list mappedDomainClassList as domainClass>
  map.put("${domainClass}", new ${domainClass}QueryBuilder());
</#list>
