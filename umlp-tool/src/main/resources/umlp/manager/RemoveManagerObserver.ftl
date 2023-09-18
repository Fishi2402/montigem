<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#assign type="${name}Observer" />
<#if delegateFlag>
  ${cd4c.method("public static boolean removeObserver(${type} obs)")}
  return getSingleton()._removeObserver(obs);
<#else>
  ${cd4c.method("public boolean _removeObserver(${type} obs)")}
  return ${name?uncap_first}Observers.remove(obs);
</#if>
