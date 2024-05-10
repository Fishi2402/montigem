<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public boolean _isLoaded${name}(long gemId)")}

<#if concreteSubclassesInclSelf?size == 0>
  return false;
<#else>
  return <#list concreteSubclassesInclSelf as subclass>this.known${subclass.name}s.has(gemId)
    && (this.known${subclass.name}s.get(gemId) != null)
    <#if !subclass?is_last>||</#if></#list>
</#if>
