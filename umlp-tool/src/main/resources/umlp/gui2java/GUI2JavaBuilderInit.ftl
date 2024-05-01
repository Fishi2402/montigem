<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "manager", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign tmpVar = "tmp">
<#assign type = cdPrinter.printType(mcType)>

${cd4c.method("protected void init${name?cap_first}()")}
if (this.${name} == null) {
  ${type} ${tmpVar} = ${manager}.${type?uncap_first}Builder();

  this.${name} = ${tmpVar};
}
