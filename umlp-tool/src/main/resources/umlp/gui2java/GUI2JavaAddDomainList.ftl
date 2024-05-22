<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getter")}
<#assign arg = "val">

${cd4c.method("protected void add${name?cap_first}(${type} ${arg})")}
this.${getter}().add(this.${getter}().size(), [${arg}.getGemId()]);