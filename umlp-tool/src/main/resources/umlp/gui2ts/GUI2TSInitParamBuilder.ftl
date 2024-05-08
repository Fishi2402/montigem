<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "builder", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign tmpVar = "tmp">

${cd4c.method("protected void ${builder}()")}
${manager}.${type?uncap_first}Builder().then(${tmpVar} => {
  this.${name} = ${tmpVar};
  ${tc.includeArgs("umlp.gui2java.GUI2JavaInitBody", [initList])}
});
