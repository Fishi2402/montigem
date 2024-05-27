<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "getter")}
<#assign idVar = "gemId">

${cd4c.method("protected void remove${name?cap_first}(long ${idVar})")}
for (let i = this.${getter}().length - 1; i >= 0; --i) {
  if (this.${getter}()[i].getGemId() == ${idVar}) {
    this.${getter}().splice(i, 1);
  }
}