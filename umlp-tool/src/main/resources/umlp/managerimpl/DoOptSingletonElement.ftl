<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
  // the gemId of a singleton does not matter
  return Optional.of(_doGet${name}());
