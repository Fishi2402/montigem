<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "initList")}
<#assign javaType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void init${name?cap_first}()")}
${javaType} ${tmpVar} = new ${javaType?uncap_first}Builder();
this.${name} = ${tmpVar};
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
