<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getterExpression", "keys", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}(${argsString})")}
for (${type} ${tmpVar}: ${getterExpression}) {
  <#list initList as init>
    this.${init.getName()}(<#list keys as key>${key.getName()}, </#list>${tmpVar});
  </#list>
}