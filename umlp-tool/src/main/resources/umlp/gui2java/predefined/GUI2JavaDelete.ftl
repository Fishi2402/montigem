<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "manager")}
<#assign param = "val">

${cd4c.method("public Consumer<Void> ${name}(${type} ${param})")}
return $ -> {
  ${manager}.delete${type}(${param}.getGemId());
};

