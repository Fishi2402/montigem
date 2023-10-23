<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}

${name} value = null;
if (${name?uncap_first}Instance.isPresent()) {
  value = ${name?uncap_first}Instance.get();
}
if (dummyProvider != null) {
  value = dummyProvider.invent${name}();
} else {
  return Optional.empty();
}

return Optional.ofNullable(value);
