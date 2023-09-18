<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#assign type="${name}Observer" />
<#if delegateFlag>
  ${cd4c.method("public static boolean removeObserver(${type} obs)")}
  return this.getSingleton()._removeObserver(obs);
<#else>
  ${cd4c.method("public boolean _removeObserver(${type} obs)")}
  const index = this.${name?uncap_first}Observers.indexOf(obs);
  if(index < 0){
    return false;
  }
  this.${name?uncap_first}Observers = this.${name?uncap_first}Observers.splice(index, 1);
  return true;
</#if>
