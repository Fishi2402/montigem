<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "removeList")}
<#assign idVar = "gemId">

${cd4c.method("protected void notify${type}Deleted(long ${idVar})")}
<#list removeList as remove>
this.${remove}(${idVar});
</#list>