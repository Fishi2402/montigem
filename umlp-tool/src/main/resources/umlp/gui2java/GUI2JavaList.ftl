<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "tmp", "listItems")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${cdPrinter.printType(mcType)} ${tmp} = new ArrayList<>();
<#list listItems as item>
${tmp}.add(${item});
</#list>