<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "setExternally", "externInit", "setter", "initList")}
<#assign javaType = cdPrinter.printType(type)>

${cd4c.method("protected void ${init}()")}
if (!this.${setExternally}) {
  this.${setter}(${manager}.get${javaType}());
}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}