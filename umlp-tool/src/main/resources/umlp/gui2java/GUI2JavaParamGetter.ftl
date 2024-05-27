<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "getter")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign type = cdPrinter.printType(mcType)>

${cd4c.method("public ${type} ${getter}()")}
return this.${name};