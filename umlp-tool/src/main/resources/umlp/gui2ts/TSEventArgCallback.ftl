<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "call", "args", "params")}
<#if params?has_content>
  <#assign paramStr = "any event, any " + params?join(", any ")>
<#else>
  <#assign paramStr = "any event">
</#if>
${cd4c.method("public void ${methodName}(${paramStr})")}
  return this.${call}(<#list args as arg>${arg}<#sep>,</#list>)(event);