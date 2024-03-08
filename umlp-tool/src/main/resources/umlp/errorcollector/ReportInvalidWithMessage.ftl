<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainName")}
${cd4c.method("public static void report${name?capFirst}InvalidWithMessage(${domainName} o, String message)")}
getInstance()._report${name?capFirst}InvalidWithMessage(o, message);