<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainName", "isAssoc")}
${cd4c.method("protected String _build${name?capFirst}InvalidMsg(${domainName} o)")}

return "${name?capFirst} <#if isAssoc>association<#else>attribute</#if> of ${domainName} with gemId " + o.getGemId() + " is invalid!";
