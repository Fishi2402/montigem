<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "id", "init", "initList")}

${cd4c.method("protected void ${init}()")}
if (this.${id} != null) {
  this.${name} = ${manager}.get${type}Sure(this.${id});
}
else {
  this.${name} = ${manager}.${type?uncap_first}Builder();
}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}