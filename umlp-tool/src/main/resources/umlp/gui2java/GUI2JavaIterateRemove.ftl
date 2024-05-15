<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getterExpression", "keys", "removeList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("protected void remove${name?cap_first}(${argsString})")}
for (${type} ${tmpVar}: ${getterExpression}) {
  <#list removeList as remove>
    this.${remove.getName()}(<#list keys as key>${key}, </#list>${tmpVar});
  </#list>
}