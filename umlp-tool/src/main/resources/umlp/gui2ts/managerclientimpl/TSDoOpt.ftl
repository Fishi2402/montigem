<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public Promise<Optional<${name}>> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
if(!this.known${subclass.name?cap_first}s.has(gemId)){
// TODO: Optimize, multiple roundtrips are possible for multiple subclasses
  this.known${subclass.name?cap_first}s.set(gemId,
    CommandManager.executeCommand(new ${subclass.name}_get(gemId)).then(v => {
      if (v.isStatusResult()) {
        return null;
      }
      let res = v.asSimpleResult();
      if (res.isSuccessful()) {
        let ${subclass.name?uncap_first}: ${subclass.name} = res.get();
        if (!${subclass.name?uncap_first}.containsObserver(this.the${subclass.name}CommandObserver)) {
          ${subclass.name?uncap_first}.addObserver(this.the${subclass.name}CommandObserver);
        }
        console.debug("Adding known ${subclass.name?uncap_first} " + gemId, LogUtil.getLoggerName());
        return ${subclass.name?uncap_first};
      }
      return null;
    })
  );
}
</#list>
return Promise.resolve(null)
<#list concreteSubclassesInclSelf as subclass>
  .then(v => {
    if (v == null) {
      return this.known${subclass.name?cap_first}s.get(gemId)
    }
    return v;
  })
</#list>