<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "getter")}
<#assign idVar = "gemId">

${cd4c.method("protected void remove${name?cap_first}(long ${idVar})")}
for (int i = this.${getter}().size() - 1; i >= 0; --i) {
  if (this.${getter}().get(i).getGemId() == ${idVar}) {
    this.${getter}().remove(i);
  }
}