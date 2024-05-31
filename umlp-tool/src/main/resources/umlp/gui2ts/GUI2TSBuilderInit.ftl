<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}()")}
${manager}.${tsType?uncap_first}Builder().then((${tmpVar}: ${tsType}) => {
  this.${name} = ${tmpVar};
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
});
