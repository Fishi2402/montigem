<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void init${name?cap_first}()")}
let ${tmpVar}: ${tsType} = new ${tsType?uncap_first}Builder();
this.${name} = ${tmpVar};
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}