<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "args", "initList")}
<#assign javaType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void init${name?cap_first}()")}
${javaType} ${tmpVar} = new ${javaType}(<#list args as arg>${arg}<#sep>, </#list>);
this.${name} = ${tmpVar};
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
