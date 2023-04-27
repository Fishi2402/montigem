<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
if(known${subclass.name?cap_first}s.containsKey(gemId)){
  return Optional.ofNullable(known${subclass.name?cap_first}s.get(gemId));
}
</#list>

SimpleResult<${name}> res = CommandManager.executeCommand(new ${name}_get(gemId)).asSimpleResult();
if (res.isSuccessful()) {
  ${name} ${name?uncap_first} = res.get();
<#list concreteSubclassesInclSelf as subclass>
  // TODO: how can we avoid instanceof here?
  // Ordered by distance between the classes, such that only the observer of the correct class is added
  <#if !subclass?is_last>
  if(${name?uncap_first} instanceof ${subclass.name}){
  <#else>
  {
  </#if>
    if (!((${subclass.name})${name?uncap_first}).containsObserver(the${subclass.name}CommandSendingObserver)) {
      ((${subclass.name})${name?uncap_first}).addObserver(the${subclass.name}CommandSendingObserver);
    }
    Log.trace("Adding known ${subclass.name?uncap_first} " + gemId, LogUtil.getLoggerName());
    known${subclass.name}s.put(${name?uncap_first}.getGemId(), (${subclass.name})${name?uncap_first});
    return Optional.of(${name?uncap_first});
  }
  <#if !subclass?is_last>else</#if>
</#list>
}
return Optional.empty();
