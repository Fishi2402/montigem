<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("remove", "getter", "removeList")}
<#assign idVar = "gemId">

${cd4c.method("protected void ${remove}(long ${idVar})")}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [removeList])}
for (int i = this.${getter}().size() - 1; i >= 0; --i) {
  if (this.${getter}().get(i).getGemId() == ${idVar}) {
    this.${getter}().remove(i);
  }
}