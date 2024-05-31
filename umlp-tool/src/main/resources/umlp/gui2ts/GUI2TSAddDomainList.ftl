<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getter")}
<#assign mcType = type.printType()>
<#assign arg = "val">

${cd4c.method("protected void add${name?cap_first}(${mcType} ${arg})")}
this.${getter}().push(${arg});