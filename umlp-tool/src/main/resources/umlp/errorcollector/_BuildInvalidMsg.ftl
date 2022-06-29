<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainName")}
${cd4c.method("protected String _build${name?capFirst}InvalidMsg(${domainName} o)")}

return "${name?capFirst} " + o.get${name?capFirst}() + " of ${domainName} with gemId " + o.getGemId() + " is invalid!";



