<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainName")}
${cd4c.method("protected void _report${name?capFirst}InvalidWithMessage(${domainName} o, String msg)")}

Log.warn(msg);
observers.forEach(obs -> obs.notify${name?capFirst}Invalid(o, msg));