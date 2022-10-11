<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static Optional<${name}> ${name?uncap_first}Opt(long gemId)")}

return getSingleton()._do${name}Opt(gemId);