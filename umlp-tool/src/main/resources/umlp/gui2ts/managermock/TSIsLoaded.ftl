<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public boolean _isLoaded${name}(long gemId)")}

<#if concreteSubclassesInclSelf?size == 0>
return false;
<#else>
return false ||
<#list concreteSubclassesInclSelf as subclass>
  this.${subclass.name?uncap_first}s.has(gemId)
  <#if !subclass?is_last>||<#else>;</#if>
</#list>
</#if>
