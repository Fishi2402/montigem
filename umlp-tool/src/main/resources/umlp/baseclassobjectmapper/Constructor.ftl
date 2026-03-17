<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classesList", "className")}
${cd4c.constructor("public ${className}()")}
  <#list classesList as fqn>
    baseDeSers.put("${fqn}", new ${fqn}DeSer());
  </#list>
