<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "initList")}
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}()")}
${manager}.${type?uncap_first}Builder().then((${tmpVar}: ${type}) => {
  this.${name} = ${tmpVar};
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
});
