<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initList")}
<#assign tmpVar = "tmp_$">
<#assign tmpVar2 = "tmp_$1">

${cd4c.method("protected void ${init}()")}
${manager}.get${type}List().then(${tmpVar} => {
  ${tmpVar}.loadAll().then(${tmpVar2} => {
    this.${name} = ${tmpVar2};
    ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
  });
});
