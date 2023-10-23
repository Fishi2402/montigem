<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name} _doGet${name}()")}
if (${name?uncap_first}Instance.isEmpty()) {
  // Construct a new instance of this singleton
  // We do not respect the dummyProvider, as only one singleton is allowed
  ${name}Builder b = ${name?uncap_first}BuilderFromId(getFreeGemId());
  ${name?uncap_first}Instance = b.build();
  if (${name?uncap_first}Instance.isEmpty()) {
    throw new RuntimeException("Unable to create a new instance for ${name} - override its builder");
  }
};
return ${name?uncap_first}Instance.get();
