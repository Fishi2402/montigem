<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz")}

CommandManager.executeCommand(new ${clazz}_${ast.name?cap_first}_get()).then(res => {
  this.${ast.name} = res.asSimpleResult().get();
});




