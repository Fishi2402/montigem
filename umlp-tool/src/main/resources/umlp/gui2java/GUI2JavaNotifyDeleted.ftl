<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "removeList")}
<#assign mcType = type.printType()>
<#assign idVar = "gemId_$">

${cd4c.method("public void notify${mcType}Deleted(long ${idVar})")}
<#list removeList as remove>
this.${remove}(${idVar});
</#list>