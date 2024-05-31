<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "addList")}
<#assign mcType = type.printType()>
<#assign javaType = cdPrinter.printType(type)>
<#assign arg = "val">

${cd4c.method("public void notify${javaType}Added(${mcType} ${arg})")}
<#list addList as add>
this.${add}(${arg});
</#list>