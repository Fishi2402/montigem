<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "manager", "init", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign type = cdPrinter.printType(mcType)>

${cd4c.method("protected void ${init}()")}
this.${name} = ${manager}.${type?uncap_first}Builder();
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
