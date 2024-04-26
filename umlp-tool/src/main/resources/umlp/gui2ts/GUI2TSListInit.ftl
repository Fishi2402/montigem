<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "listItems")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}()")}
if (this.${name} == null) {
  const ${tmpVar}: ${cdPrinter.printType(mcType)} = [];
  <#list listItems as item>
  ${tmpVar}.push(${item});
  </#list>

  this.${name} = ${tmpVar};
}