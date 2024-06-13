<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initList")}
<#assign javaType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void ${init}()")}
if (this.${name} === undefined) {
  ${manager}.get${javaType}().then(${tmpVar} => {
    if (this.${name} === undefined) {
      this.${name} = ${tmpVar};
    }
    ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
  });
}
else {
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
}