<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
if(known${subclass.name?cap_first}s.containsKey(gemId)){
  return Optional.ofNullable(known${subclass.name?cap_first}s.get(gemId));
}
</#list>
// TODO: Optimize, multiple roundtrips are possible for multiple subclasses
<#list concreteSubclassesInclSelf as subclass>
{
  SimpleResult<${subclass.name}> res = CommandManager.executeCommand(new ${subclass.name}_get(gemId)).asSimpleResult();
  if (res.isSuccessful()) {
    ${subclass.name} ${subclass.name?uncap_first} = res.get();
    if (!(${subclass.name?uncap_first}).containsObserver(the${subclass.name}CommandObserver)) {
      (${subclass.name?uncap_first}).addObserver(the${subclass.name}CommandObserver);
    }
    Log.trace("Adding known ${subclass.name?uncap_first} " + gemId, LogUtil.getLoggerName());
    known${subclass.name}s.put(${subclass.name?uncap_first}.getGemId(), ${subclass.name?uncap_first});
    return Optional.of(${subclass.name?uncap_first});
  }
}
</#list>
return Optional.empty();
