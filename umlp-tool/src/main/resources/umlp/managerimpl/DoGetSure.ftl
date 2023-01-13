<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name} _doGet${name}Sure(long gemId)")}

<#-- TODO: implement caching -->
return _do${name}Opt(gemId).get();