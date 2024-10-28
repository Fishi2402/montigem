<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "setExternally", "initList")}
<#assign javaType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void ${init}()")}
if (!this.${setExternally}) {
  ${manager}.get${javaType}().then(${tmpVar} => {
    this.${name} = ${tmpVar};
    ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
  });
}
else {
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
}