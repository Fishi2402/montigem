<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public ${name} _doGet${name}Sure(long gemId)")}

<#-- TODO: implement caching -->
return this._do${name}Opt(gemId);