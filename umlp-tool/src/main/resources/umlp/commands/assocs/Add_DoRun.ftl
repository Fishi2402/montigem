<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName","sourceType","targetType","definition")}
${cd4c.method("public StatusResult doRun()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>
boolean loaded${ctargetName} = ${definition}Manager.isLoaded${targetType}(${targetName}GemId);
boolean loaded${csourceName} = ${definition}Manager.isLoaded${sourceType}(${sourceName}GemId);

// TODO: With DB: server must have loaded both
assert CommandManager.getParticipantId() != 0 || (loaded${ctargetName} && loaded${csourceName});

if(loaded${ctargetName} && loaded${csourceName}){
  ${sourceType} p = ${definition}Manager.get${sourceType}Sure(${sourceName}GemId);
  ${targetType} a = ${definition}Manager.get${targetType}Sure(${targetName}GemId);

  // TODO: only in server?
  if(${sourceName}Index == -1){
    ${sourceName}Index = p.size${ctargetName}s();
  }

  if(${targetName}Index == -1){
    ${targetName}Index = a.size${csourceName}s();
  }

  a.add${csourceName}(${targetName}Index, p, NotificationScope.MINIMAL);
  p.add${ctargetName}(${sourceName}Index, a, NotificationScope.MINIMAL);
}else{
  // Do not trigger load of ${csourceName}. If the ${ctargetName} is loaded, we can change it instead and avoid a load operation
  if(loaded${ctargetName}){
    ${targetType} a = ${definition}Manager.get${targetType}Sure(${targetName}GemId);
    if(${targetName}Index == -1){
      ${targetName}Index = a.size${csourceName}s();
    }
    a.INTERNAL_add${csourceName}Id(${targetName}Index, ${sourceName}GemId, NotificationScope.MINIMAL);
  } else if(loaded${csourceName}) {
    ${sourceType} p = ${definition}Manager.get${sourceType}Sure(${sourceName}GemId);
    if(${sourceName}Index == -1){
      ${sourceName}Index = p.size${ctargetName}s();
    }
    p.INTERNAL_add${ctargetName}Id(${sourceName}Index, ${targetName}GemId, NotificationScope.MINIMAL);
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
}

return StatusResult.ok(getId());