<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initCheck", "initList")}
<#assign javaType = cdPrinter.printType(type)>

${cd4c.method("protected void ${init}()")}
if (!this.${initCheck}) {
  this.${name} = ${manager}.get${javaType}List();
}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}