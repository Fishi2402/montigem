<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainName")}
${cd4c.method("public static void report${name?capFirst}Invalid(${domainName} o)")}

this.getInstance()._report${name?capFirst}Invalid(o);