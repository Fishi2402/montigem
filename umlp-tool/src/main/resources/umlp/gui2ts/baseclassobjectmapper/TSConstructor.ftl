<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classesList", "initializationTypeName")}
${cd4c.constructor("public ${initializationTypeName}()")}
  <#list classesList as fqn>
    this.baseDeSers.set("${fqn}", new ${fqn}DeSer());
  </#list>
