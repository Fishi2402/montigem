<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "id", "init", "initList")}
<#assign javaType = cdPrinter.printType(type)>

${cd4c.method("protected void ${init}()")}
if (this.${id} != null) {
  this.${name} = ${manager}.get${javaType}Sure(this.${id});
}
else {
  this.${name} = ${manager}.${javaType?uncap_first}Builder();
}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}