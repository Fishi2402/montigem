<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourcename","targetname","definition")}
${cd4c.method("public StatusResult doRun()")}

boolean loaded${targetname} = ${definition}Manager.isLoaded${targetname}(${targetname}GemId);
boolean loaded${sourcename} = ${definition}Manager.isLoaded${sourcename}(${sourcename}GemId);

// TODO: With DB: server must have loaded both
assert CommandManager.getParticipantId() != 0 || (loaded${targetname} && loaded${sourcename});

if(loaded${targetname} && loaded${sourcename}){
  ${sourcename} p = ${definition}Manager.get${sourcename}Sure(${sourcename}GemId);
  ${targetname} a = ${definition}Manager.get${targetname}Sure(${targetname}GemId);

  // TODO: only in server?
  if(indexIn${sourcename}List == -1){
    // TODO: this might be the wrong index if the list contains an object twice!
    indexIn${sourcename}List = p.getVisitedGemList().indexOfId(${targetname}GemId);
  }

  if(indexIn${targetname}List == -1){
    indexIn${targetname}List = a.getVisitorGemList().indexOfId(${sourcename}GemId);
  }

  a.removeVisitorGem(indexIn${targetname}List, NotificationScope.MINIMAL);
  p.removeVisitedGem(indexIn${sourcename}List, NotificationScope.MINIMAL);
}else{
  // Do not trigger load of ${sourcename}. If the ${targetname} is loaded, we can change it instead and avoid a load operation
  if(loaded${targetname}){
    ${targetname} a = ${definition}Manager.get${targetname}Sure(${targetname}GemId);
    if(indexIn${targetname}List == -1){
      indexIn${targetname}List = a.getVisitorGemList().indexOfId(${sourcename}GemId);
    }
    a.removeVisitorGem(indexIn${targetname}List, NotificationScope.MINIMAL);
  } else if(loaded${sourcename}) {
    ${sourcename} p = ${definition}Manager.get${sourcename}Sure(${sourcename}GemId);
    if(indexIn${sourcename}List == -1){
      // TODO: this might be the wrong index if the list contains an object twice!
      indexIn${sourcename}List = p.getVisitedGemList().indexOfId(${targetname}GemId);
    }
    p.removeVisitedGem(indexIn${sourcename}List, NotificationScope.MINIMAL);
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
}

return StatusResult.ok(getId());