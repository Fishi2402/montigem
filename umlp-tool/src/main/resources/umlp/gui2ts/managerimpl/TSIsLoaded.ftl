<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public boolean _isLoaded${name}(long gemId)")}

return <#list concreteSubclassesInclSelf as subclass>this.known${subclass.name}s.has(gemId)<#if !subclass?is_last>||</#if></#list>
<#if concreteSubclassesInclSelf?size == 0>false</#if>
;
