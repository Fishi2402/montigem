<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static Gem<${name}> create${name}Gem(long gemId)")}

return getSingleton()._doCreate${name}Gem(gemId);