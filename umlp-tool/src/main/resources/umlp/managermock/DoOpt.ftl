<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}

${name} value;
if (${name?uncap_first}s.containsKey(gemId)) {
  value = ${name?uncap_first}s.get(gemId);
} else if (dummyProvider != null) {
  value = dummyProvider.invent${name}();
} else {
  return Optional.empty();
}

if (!value.containsObserver(the${name}CommandObserver)) {
  value.addObserver(the${name}CommandObserver);
}

return Optional.ofNullable(value);
