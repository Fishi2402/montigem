<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName","sourceType","targetType",
                "isSourceList","isTargetList","managerName", "hasOCL")}
${cd4c.method("public StatusResult doAction()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>

boolean loaded${ctargetName} = ${managerName}.isLoaded${targetType}(${targetName}GemId);
boolean loaded${csourceName} = ${managerName}.isLoaded${sourceType}(${sourceName}GemId);

if(loaded${ctargetName} && loaded${csourceName}){
  ${sourceType} p = ${managerName}.get${sourceType}Sure(${sourceName}GemId);
  ${targetType} a = ${managerName}.get${targetType}Sure(${targetName}GemId);

  // TODO: only in server?
<#if isTargetList>
  if(${targetName}Index == -1){
    ${targetName}Index = p._internal_get${ctargetName}GemList().indexOfId(${targetName}GemId);
  } else if (p._internal_get${ctargetName}GemList().get(${targetName}Index).getGemId() != ${targetName}GemId) {
    // The client appears to have a different state than the backend
    // This error is unrecoverable!
    Log.warn("Inconsistent state: " +  this.getParticipantId() + " tried to remove " + ${targetName}Index + "/" + p._internal_get${ctargetName}GemList().get(${targetName}Index).getGemId());
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
  p.remove${ctargetName}(${targetName}Index, NotificationScope.MINIMAL);
<#else>
  p._internal_set${ctargetName}(-1, NotificationScope.MINIMAL);
</#if>

<#if isSourceList>
  if(${sourceName}Index == -1){
    ${sourceName}Index = a._internal_get${csourceName}GemList().indexOfId(${sourceName}GemId);
  }
  a.remove${csourceName}(${sourceName}Index, NotificationScope.MINIMAL);
<#else>
  a._internal_set${csourceName}(-1, NotificationScope.MINIMAL);
</#if>
}else{
  // Do not trigger load of ${sourceName}. If the ${targetName} is loaded, we can change it instead and avoid a load operation
  if(loaded${ctargetName}){
    ${targetType} a = ${managerName}.get${targetType}Sure(${targetName}GemId);
<#if isSourceList>
    // TODO: Why do we use ${targetName}(targetName) instead of ${sourceName}(sourceName) here?
    if(${targetName}Index == -1){
      ${targetName}Index = a._internal_get${csourceName}GemList().indexOfId(${targetName}GemId);
    }
    a.remove${csourceName}(${targetName}Index, NotificationScope.MINIMAL);
<#else>
  a._internal_set${csourceName}(-1, NotificationScope.MINIMAL);
</#if>
  } else if(loaded${csourceName}) {
    ${sourceType} p = ${managerName}.get${sourceType}Sure(${sourceName}GemId);
<#if isTargetList>
    if(${sourceName}Index == -1){
      ${sourceName}Index = p._internal_get${ctargetName}GemList().indexOfId(${sourceName}GemId);
    }
    p.remove${ctargetName}(${sourceName}Index, NotificationScope.MINIMAL);
<#else>
  p._internal_set${ctargetName}(-1, NotificationScope.MINIMAL);
</#if>
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
}

return StatusResult.ok(getId());
