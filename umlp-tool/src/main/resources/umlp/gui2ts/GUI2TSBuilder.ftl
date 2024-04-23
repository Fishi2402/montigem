<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "tmp", "manager")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

const ${tmp}: ${cdPrinter.printType(mcType)} = ${manager}.${type?uncap_first}Builder();
