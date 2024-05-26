<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}
<#assign param = "val">

${cd4c.method("public Consumer<Void> ${name}(${type} ${param})")}
return $ => {
  ${param}.build();
};