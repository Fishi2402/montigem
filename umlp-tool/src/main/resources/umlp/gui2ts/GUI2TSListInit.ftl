<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "listItems", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}()")}
const ${tmpVar}: ${cdPrinter.printType(mcType)} = [];
<#list listItems as item>
${tmpVar}.push(${item});
</#list>

this.${name} = ${tmpVar};
${tc.includeArgs("umlp.gui2java.GUI2JavaCallInit", [initList])}