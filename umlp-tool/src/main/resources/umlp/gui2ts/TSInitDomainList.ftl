<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainVar", "domainVarType", "manager")}
this.update${domainVar?cap_first}();
CommandManager.executeCommand(new ${domainVarType}_subscribeManagerEvents())

