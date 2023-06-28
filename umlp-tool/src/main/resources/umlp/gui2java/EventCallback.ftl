<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "call", "args")}
<#-- TODO AGe: args, events in iterate, types -->
<#assign params = "Object " + args?join(", Object ")>
<#if call?has_content>
${cd4c.method("public void ${methodName}(Object event, ${params})")}
  return this.${call}(<#list args as arg>${arg}<#if !arg?is_last>,</#if></#list>).accept(event);
<#else>
${cd4c.method("public Consumer<Object> ${methodName}(${params})")}
  return event -> {};
</#if>