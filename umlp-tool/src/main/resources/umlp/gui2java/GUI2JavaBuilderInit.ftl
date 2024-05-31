<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "initList")}
<#assign javaType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}()")}
${javaType} ${tmpVar} = ${manager}.${javaType?uncap_first}Builder();
this.${name} = ${tmpVar};
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
