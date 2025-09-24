<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classesList", "initializationTypeName")}
${cd4c.constructor("public ${initializationTypeName}()")}
  <#list classesList as fqn>
    baseDeSers.put("${fqn}", new ${fqn}DeSer());
  </#list>
