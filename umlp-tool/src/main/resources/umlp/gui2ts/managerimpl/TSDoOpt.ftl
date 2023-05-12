<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
  if(this.known${subclass.name?cap_first}s.containsKey(gemId)){
    return this.known${subclass.name?cap_first}s.get(gemId);
  }
</#list>
  return null;
