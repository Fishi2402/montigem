<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name")}
${cd4c.method("boolean check${name?cap_first}()")}

boolean res = true;

if(${name} == null){
  res = false;
  ${domain}ErrorCollector.report${name?cap_first}Invalid(this);
}

return res;





