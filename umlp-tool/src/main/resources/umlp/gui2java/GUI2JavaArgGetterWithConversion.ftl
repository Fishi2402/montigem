<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "nullCheck", "returnValue", "conversion", "args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(args)>

${cd4c.method("public ${type} get${name?cap_first}(${argsString})")}
if (${nullCheck}) {
  return null;
}
${conversion};
return ${returnValue};