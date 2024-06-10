<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "manager")}
<#assign mcType = type.printType()>
<#assign tsType = cdPrinter.printType(type)>
<#assign param = "val">

${cd4c.method("public Consumer<Void> ${name}(${mcType} ${param})")}
return $ => {
  ${manager}.delete${tsType}(${param}.getGemId());
};

