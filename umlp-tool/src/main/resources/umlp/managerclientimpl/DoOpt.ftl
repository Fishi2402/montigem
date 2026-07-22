<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
<#list concreteSubclassesInclSelf as subclass>
if(known${subclass.name?cap_first}s.containsKey(gemId)){
  return Optional.ofNullable(known${subclass.name?cap_first}s.get(gemId));
}
</#list>
// Single roundtrip: the server resolves the concrete subtype, the "@type"
// discriminator materializes it on this side, and _registerMe() routes the
// object into the manager owning its concrete type (monticore#5062).
CommandResult<${name}> commandResult = CommandManager.executeCommand(new ${name}_get(gemId));

// avoid race condition
<#list concreteSubclassesInclSelf as subclass>
if(known${subclass.name?cap_first}s.containsKey(gemId)){
  return Optional.ofNullable(known${subclass.name?cap_first}s.get(gemId));
}
</#list>

if (commandResult.isSimpleResult()) {
  SimpleResult<${name}> res = commandResult.asSimpleResult();
  if (res.isSuccessful()) {
    return Optional.of(res.get()._registerMe());
  }
}
Log.warn("0x12002: Could not load ${name} with gemId "+gemId);
return Optional.empty();
