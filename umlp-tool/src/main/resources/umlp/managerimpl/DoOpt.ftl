<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","name", "concreteSubclassesInclSelf")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
  if(known${subclass.name?cap_first}s.containsKey(gemId)){
    return Optional.ofNullable(known${subclass.name?cap_first}s.get(gemId));
  }
</#list>
<#list concreteSubclassesInclSelf as subclass>
  {
    Optional<${subclass.name}> res = ${cdName}DAOFactory.get${subclass.name?cap_first}DAO().select${subclass.name}(gemId);
    if(res.isPresent()){
      ${subclass.name} h = res.get();
      _doAddRequiredObserver${subclass.name}(h);
      known${subclass.name}s.put(h.getGemId(), h);
      return Optional.of(h);
    }
  }
</#list>
// debug, not warn: delegation managers probe this method for each subtype while
// resolving polymorphic associations; a miss here is expected. The delegating
// _do<Type>Opt warns once if the object is found nowhere.
Log.debug("0x12002: Could not load ${name} with gemId "+gemId, "${name}Manager");
return Optional.empty();
