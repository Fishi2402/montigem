<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected boolean _isLoaded${name}(long gemId)")}

<#if concreteSubclassesInclSelf?size == 0>
return false;
<#else>
return
<#list concreteSubclassesInclSelf as subclass>
  ${subclass.name?uncap_first}s.containsKey(gemId)
  <#if ! subclass?is_last>||<#else>;</#if>
</#list>
</#if>
