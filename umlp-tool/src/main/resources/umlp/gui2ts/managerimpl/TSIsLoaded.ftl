<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected boolean _isLoaded${name}(long gemId)")}

return
<#list concreteSubclassesInclSelf as subclass>
  this.known${subclass.name}s.containsKey(gemId)
  <#if !subclass?is_last>||</#if>
</#list>
<#if concreteSubclassesInclSelf?size == 0>false</#if>
;
