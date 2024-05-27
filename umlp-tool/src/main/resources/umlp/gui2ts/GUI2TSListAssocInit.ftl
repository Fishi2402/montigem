<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "getterExpression", "keys", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("public void init${name?cap_first}(${argsString})")}
${getterExpression}.loadAll().then(${tmpVar} => {
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
});