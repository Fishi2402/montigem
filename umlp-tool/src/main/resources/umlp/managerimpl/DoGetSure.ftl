<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name} _doGet${name}Sure(long gemId)")}

<#-- TODO: implement caching -->
Optional<${name}> opt = _do${name}Opt(gemId);
if(opt.isEmpty()) {
  Log.error("0x12001: Could not load ${name} with gemId "+gemId);
}
return opt.get();
