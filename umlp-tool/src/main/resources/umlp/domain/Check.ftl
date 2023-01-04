<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "reportError")}
${cd4c.method("protected boolean check${name?cap_first}()")}

boolean res = true;

<#if reportError>
if(${name} == null){
  res = false;
  ${domain}ErrorCollector.report${name?cap_first}Invalid((${domain}) this);
}
</#if>

return res;