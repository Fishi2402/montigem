<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "nullCheck", "setterExpression", "args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(type, args)>

${cd4c.method("public void set${name?cap_first}(${argsString})")}
if (${nullCheck}) {
  return;
}
${setterExpression};
