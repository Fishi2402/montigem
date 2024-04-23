<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "tmp", "manager")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${cdPrinter.printType(mcType)} ${tmp} = ${manager}.${type?uncap_first}Builder();
