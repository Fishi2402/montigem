<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public Promise<Optional<${name}>> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
if(this.known${subclass.name?cap_first}s.has(gemId)){
  return Promise.resolve(this.known${subclass.name?cap_first}s.get(gemId));
}
</#list>
// TODO: Optimize, multiple roundtrips are possible for multiple subclasses
<#list concreteSubclassesInclSelf as subclass>
  return CommandManager.executeCommand(new ${subclass.name}_get(gemId)).then(v => {
    let res = v.asSimpleResult();
    if (res.isSuccessful()) {
      let ${subclass.name?uncap_first}: ${subclass.name} = res.get();
      if (!(${subclass.name?uncap_first}).containsObserver(this.the${subclass.name}CommandObserver)) {
        (${subclass.name?uncap_first}).addObserver(this.the${subclass.name}CommandObserver);
      }
      console.debug("Adding known ${subclass.name?uncap_first} " + gemId, LogUtil.getLoggerName());
      this.known${subclass.name}s.set(${subclass.name?uncap_first}.getGemId(), ${subclass.name?uncap_first});
      return ${subclass.name?uncap_first};
    }
    return null;
  });
</#list>
