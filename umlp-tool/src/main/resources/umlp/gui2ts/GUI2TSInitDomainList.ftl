<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initCheck", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">
<#assign tmpVar2 = "tmp_$1">

${cd4c.method("protected void ${init}()")}
if (!this.${initCheck}) {
  ${manager}.get${tsType}List().then(${tmpVar} => {
    ${tmpVar}.loadAll().then(${tmpVar2} => {
      this.${name} = ${tmpVar2};
      ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
    });
  });
}
else {
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
}