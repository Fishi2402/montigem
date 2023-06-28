<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "reportError", "isAssocList")}
${cd4c.method("protected boolean check${name?cap_first}()")}

boolean res = true;

<#if reportError>
if(<#if isAssocList>_internal_get${name?cap_first}GemList()<#else>_${name}</#if> == null){
  res = false;
  ${domain}ErrorCollector.report${name?cap_first}Invalid((${domain}) this);
}
</#if>

return res;
