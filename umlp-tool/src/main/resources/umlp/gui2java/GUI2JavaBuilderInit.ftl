<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "manager", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}()")}
if (this.${name} == null) {
  ${cdPrinter.printType(mcType)} ${tmpVar} = ${manager}.${type?uncap_first}Builder();

  this.${name} = ${tmpVar};
}
