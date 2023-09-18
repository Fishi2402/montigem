<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainVar", "domainVarType", "manager")}
this.update${domainVarType}s();
CommandManager.executeCommand(new ${domainVarType}_subscribeManagerEvents())

