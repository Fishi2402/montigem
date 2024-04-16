<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "call", "args", "params")}
<#-- TODO AGe: args, events in iterate, types -->
<#if params?has_content>
  <#assign paramStr = "Object event, Object " + params?join(", Object ")>
<#else>
  <#assign paramStr = "Object event">
</#if>
${cd4c.method("public void ${methodName}(${paramStr})")}
  this.${call}(<#list args as arg>${arg}<#if !arg?is_last>,</#if></#list>).accept(event);
