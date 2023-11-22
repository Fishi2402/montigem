<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}

let value: ${name} = null;
if (${name?uncap_first}Instance != null) {
  value = ${name?uncap_first}Instance;
}

return value;
