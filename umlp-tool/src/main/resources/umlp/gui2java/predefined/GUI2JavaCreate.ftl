<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
<#assign param = "val">

${cd4c.method("public Consumer<Void> create(${type} ${param})")}
return $ -> {
  ${param}.build();
}