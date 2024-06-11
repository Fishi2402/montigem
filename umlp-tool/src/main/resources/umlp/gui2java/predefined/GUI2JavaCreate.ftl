<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}
<#assign mcType = type.printType()>
<#assign param = "val">

${cd4c.method("public Consumer<Void> ${name}(${mcType} ${param})")}
return $ -> {
  ${param}.build();
};