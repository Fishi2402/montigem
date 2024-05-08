<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initList")}
<#assign tmpVar = "tmp">

${cd4c.method("protected void ${init}()")}
${manager}.get${type}List().then(${tmpVar} => {
  this.${name} = ${tmpVar};
  ${tc.includeArgs("umlp.gui2java.GUI2JavaInitBody", [initList])}
});
