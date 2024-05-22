<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "getter")}
<#assign idVar = "gemId">

${cd4c.method("protected void add${name?cap_first}(long ${idVar})")}
this.${getter}().add()