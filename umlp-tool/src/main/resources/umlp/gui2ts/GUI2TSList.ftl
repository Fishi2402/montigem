<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "tmp", "listItems")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

const ${tmp}: ${cdPrinter.printType(mcType)} = [];
<#list listItems as item>
${tmp}.push(${item});
</#list>