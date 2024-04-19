<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "listItems")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign tmpVar = "tmp">

${cd4c.method("protected void sync${name?cap_first}()")}
${tc.includeArgs("umlp.gui2java.GUI2JavaList", [type, tmpVar, listItems])}
if (this.${name} == null) {
  this.${name} = ${tmpVar};
}