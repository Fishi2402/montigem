<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
  if(known${subclass.name?cap_first}s.containsKey(gemId)){
    return Optional.ofNullable(known${subclass.name?cap_first}s.get(gemId));
  }
</#list>
<#list concreteSubclassesInclSelf as subclass>
  {
    Optional<${subclass.name}> res = ${subclass.name?uncap_first}DAO.select${subclass.name}(gemId);
    if(res.isPresent()){
      ${subclass.name} h = res.get();
      if(!h.containsObserver(the${subclass.name}DbObserver)){
        h.addObserver(the${subclass.name}DbObserver);
      }
      known${subclass.name}s.put(h.getGemId(), h);
      return Optional.of(h);
    }
  }
</#list>
  return Optional.empty();
