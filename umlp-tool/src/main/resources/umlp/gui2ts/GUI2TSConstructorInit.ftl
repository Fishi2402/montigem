<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "args", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void init${name?cap_first}()")}
const ${tmpVar}: ${tsType} = new ${tsType}(<#list args as arg>${arg}<#sep>, </#list>);
this.${name} = ${tmpVar};
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
