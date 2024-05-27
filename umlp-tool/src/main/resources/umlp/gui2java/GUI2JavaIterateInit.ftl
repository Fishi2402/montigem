<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "item", "getterExpression", "keys", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>

${cd4c.method("protected void init${name?cap_first}(${argsString})")}
for (${type} ${item} : ${getterExpression}) {
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
}