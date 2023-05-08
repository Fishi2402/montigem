<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "call")}
<#-- TODO AGe: args, events in iterate -->
${cd4c.method("public void ${methodName}()")}
<#if call?has_content>this.${call};</#if>