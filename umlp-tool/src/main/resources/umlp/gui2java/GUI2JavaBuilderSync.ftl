<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign tmpVar = "tmp">

${cd4c.method("protected void sync${name?cap_first}()")}
${tc.includeArgs("umlp.gui2java.GUI2JavaBuilder", [type, tmpVar, manager])}
if (this.${name} == null) {
  this.${name} = ${tmpVar};
}
