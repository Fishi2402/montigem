<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("manager", "type")}
<#assign param = "val">

${cd4c.method("public void create(${type} ${param})")}
return $ -> {
  ${manager}.add${type}(${param}.build());
};
