<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "call")}
<#-- TODO AGe: args, events in iterate, types -->
${cd4c.method("public void ${methodName}(Object event)")}
<#if call?has_content>this.${call}(event);</#if>