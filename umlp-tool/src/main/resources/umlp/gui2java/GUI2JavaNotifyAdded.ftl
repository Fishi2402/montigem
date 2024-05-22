<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "addList")}
<#assign arg = "val">

${cd4c.method("protected void notify${type}Deleted(${type} ${arg})")}
<#list addList as add>
this.${add}(${arg});
</#list>