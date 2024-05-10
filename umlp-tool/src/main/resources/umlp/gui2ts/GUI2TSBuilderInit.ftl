<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "manager", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}()")}
${manager}.${type?uncap_first}Builder().then((${tmpVar}: ${cdPrinter.printType(mcType)}) => {
  this.${name} = ${tmpVar};
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallInit", [initList])}
});
