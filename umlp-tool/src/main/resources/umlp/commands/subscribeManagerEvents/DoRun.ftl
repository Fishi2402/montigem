<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("managerName", "observerName", "type")}
${cd4c.method("public StatusResult doRun()")}

CommandManager.subscribeToType(getParticipantId(), new ${type}Filter());
return StatusResult.ok(getId());
