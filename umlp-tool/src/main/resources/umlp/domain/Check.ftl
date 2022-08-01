<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name")}
${cd4c.method("public boolean check${name?cap_first}()")}

boolean res = true;

if(${name} == null){
  res = false;
  ${domain}ErrorCollector.report${name?cap_first}Invalid((${domain}) this);
}

return res;





