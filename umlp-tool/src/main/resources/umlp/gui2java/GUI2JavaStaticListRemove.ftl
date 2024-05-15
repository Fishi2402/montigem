<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "getter", "keys", "removeList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign idVar = "gemId">
<#assign tmpVar = "tmp">

${cd4c.method("public void remove${name?cap_first}(long ${idVar}, ${argsString})")}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [removeList])}
var ${tmpVar} = this.${getter}()
<#list keys as key>
  .get(${key.getName()})
</#list>
;

for (int i = ${tmpVar}.size() - 1; i >= 0; --i) {
  if (${tmpVar}.get(i).getGemId() == ${idVar}) {
    ${tmpVar}.remove(i);
  }
}