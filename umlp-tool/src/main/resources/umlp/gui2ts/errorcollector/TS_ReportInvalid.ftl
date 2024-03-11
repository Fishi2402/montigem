<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainName")}
${cd4c.method("protected void _report${name?capFirst}Invalid(${domainName} o, string msg)")}
console.warn(msg);
this.observers.forEach(obs => obs.notify${name?capFirst}Invalid(o, msg));