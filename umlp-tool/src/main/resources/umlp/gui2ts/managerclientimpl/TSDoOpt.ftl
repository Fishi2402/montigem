<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public Optional<${name}> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
if(this.known${subclass.name?cap_first}s.has(gemId)){
  return this.known${subclass.name?cap_first}s.get(gemId);
}
</#list>
// TODO: Optimize, multiple roundtrips are possible for multiple subclasses
<#list concreteSubclassesInclSelf as subclass>
{
  let res: SimpleResult<${subclass.name}> = CommandManager.executeCommand(new ${subclass.name}_get(gemId)).asSimpleResult();
  if (res.isSuccessful()) {
    let ${subclass.name?uncap_first}: ${subclass.name} = res.get();
    if (!(${subclass.name?uncap_first}).containsObserver(this.the${subclass.name}CommandObserver)) {
      (${subclass.name?uncap_first}).addObserver(this.the${subclass.name}CommandObserver);
    }
    console.debug("Adding known ${subclass.name?uncap_first} " + gemId, LogUtil.getLoggerName());
    console.trace();
    this.known${subclass.name}s.set(${subclass.name?uncap_first}.getGemId(), ${subclass.name?uncap_first});
    return ${subclass.name?uncap_first};
  }
}
</#list>
return null;
