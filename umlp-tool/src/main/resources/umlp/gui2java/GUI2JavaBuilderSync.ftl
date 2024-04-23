<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "manager")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign tmpVar = "tmp">

${cd4c.method("protected void sync${name?cap_first}()")}
${tc.includeArgs("umlp.gui2java.GUI2JavaBuilder", [mcType, tmpVar, manager])}
if (this.${name} == null) {
  this.${name} = ${tmpVar};
}
