<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "params")}
<#assign mcType = type.printType()>
<#assign paramString = genHelper.params2String(params)>
<#assign paramVarsString = genHelper.params2StringVariableNames(params)>

${cd4c.method("public ${mcType} ${name}(${paramString})")}

CommandResult<${mcType}> res = CommandManager.executeCommand(new ${name?cap_first}(${paramVarsString}));

if(res.isSuccessful() && res.isSimpleResult()) {
  return res.asSimpleResult().get();
}
return null;
