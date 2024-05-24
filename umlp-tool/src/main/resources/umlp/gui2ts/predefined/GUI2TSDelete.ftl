<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "manager")}
<#assign param = "val">

${cd4c.method("public Consumer<Void> delete(${type} ${param})")}
return $ => {
  ${manager}.delete${type}(${param}.getGemId());
};

