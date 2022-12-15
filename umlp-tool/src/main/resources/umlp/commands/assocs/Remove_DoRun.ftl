<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName","sourceType","targetType",
                "isSourceList","isTargetList","definition")}
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
<#if isTargetList>
  if(${sourceName}Index == -1){
    ${sourceName}Index = p._internal_get${ctargetName}GemList().indexOfId(${targetName}GemId);
  }
  p.remove${ctargetName}(${sourceName}Index, NotificationScope.MINIMAL);
<#else>
  // TODO
</#if>

<#if isSourceList>
  if(${targetName}Index == -1){
    ${targetName}Index = a._internal_get${csourceName}GemList().indexOfId(${sourceName}GemId);
  }
  a.remove${csourceName}(${targetName}Index, NotificationScope.MINIMAL);
<#else>
  // TODO
</#if>

}else{
  // Do not trigger load of ${sourceName}. If the ${targetName} is loaded, we can change it instead and avoid a load operation
  if(loaded${ctargetName}){
    ${targetType} a = ${definition}Manager.get${targetType}Sure(${targetName}GemId);
<#if isSourceList>
    if(${targetName}Index == -1){
      ${targetName}Index = a._internal_get${csourceName}GemList().indexOfId(${sourceName}GemId);
    }
    a.remove${csourceName}(${targetName}Index, NotificationScope.MINIMAL);
<#else>
    // TODO
</#if>
  } else if(loaded${csourceName}) {
    ${sourceType} p = ${definition}Manager.get${sourceType}Sure(${sourceName}GemId);
<#if isTargetList>
    if(${sourceName}Index == -1){
      ${sourceName}Index = p._internal_get${ctargetName}GemList().indexOfId(${targetName}GemId);
    }
    p.remove${ctargetName}(${sourceName}Index, NotificationScope.MINIMAL);
<#else>
  // TODO
</#if>
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
}

return StatusResult.ok(getId());