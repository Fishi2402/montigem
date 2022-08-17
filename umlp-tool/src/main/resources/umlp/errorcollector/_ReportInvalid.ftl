<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainName")}
${cd4c.method("protected void _report${name?capFirst}Invalid(${domainName} o)")}

String msg = _build${name?capFirst}InvalidMsg(o);
Log.warn(msg);
observers.forEach(obs -> obs.notify${name?capFirst}Invalid(o, msg));





