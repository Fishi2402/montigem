<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("managerName", "observerName", "type")}
${cd4c.method("public StatusResult doAction()")}

CommandManager.subscribeToType(getParticipantId(), new ${type}Filter());
return StatusResult.ok(getId());
