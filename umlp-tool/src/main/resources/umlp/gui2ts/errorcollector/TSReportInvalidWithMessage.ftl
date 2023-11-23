<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainName")}
${cd4c.method("public static void report${name?capFirst}InvalidWithMessage(${domainName} o, string message)")}
this.getInstance()._report${name?capFirst}Invalid(o, message);