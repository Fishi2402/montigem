<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "nullCheck", "setterExpression", "args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(args)>

${cd4c.method("protected void set${name?cap_first}(
  ${type} val <#if argsString?has_content>,</#if> ${argsString}
)")}
if (${nullCheck}) {
  return null;
}
return ${setterExpression};
