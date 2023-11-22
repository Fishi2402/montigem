<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name} _doGet${name}OrInitSingleton(java.util.function.Consumer<${name}Builder> initializer)")}
if (${name?uncap_first}Instance.isEmpty()) {
  ${name}Builder b = ${name?uncap_first}BuilderFromId(getFreeGemId());

  initializer.accept(b);

  ${name?uncap_first}Instance = b.build();
  if (${name?uncap_first}Instance.isEmpty())
    throw new RuntimeException("XX");
};
return ${name?uncap_first}Instance.get();
