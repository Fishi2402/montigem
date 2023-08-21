<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "reportError")}
${cd4c.method("protected boolean check${name?cap_first}()")}

boolean res = true;

<#if reportError>
if(_${name} == null){
  res = false;
  ${domain}ErrorCollector.report${name?cap_first}Invalid((${domain}) this);
}else {
  if(_${name}.size()<1){
    res = false;
    ${domain}ErrorCollector.report${name?cap_first}Invalid((${domain}) this);
  }
}
</#if>

return res;
