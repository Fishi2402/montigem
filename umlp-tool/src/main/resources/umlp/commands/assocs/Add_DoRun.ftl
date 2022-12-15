<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assoc","sourceName","targetName","sourceType","targetType",
               "isSourceList","isTargetList","definition")}
${cd4c.method("public StatusResult doRun()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>
boolean loaded${ctargetName} = ${definition}Manager.isLoaded${targetType}(${targetName}GemId);
boolean loaded${csourceName} = ${definition}Manager.isLoaded${sourceType}(${sourceName}GemId);

// TODO: With DB: server must have loaded both
assert CommandManager.getParticipantId() != 0 || (loaded${ctargetName} && loaded${csourceName});

if(loaded${ctargetName} && loaded${csourceName}){
  ${sourceType} source = ${definition}Manager.get${sourceType}Sure(${sourceName}GemId);
  ${targetType} target = ${definition}Manager.get${targetType}Sure(${targetName}GemId);

  // TODO: only in server?
<#if isSourceList>
  if(${sourceName}Index == -1){
    ${sourceName}Index = target.size${csourceName}s();
  }
</#if>

<#if isTargetList>
  if(${targetName}Index == -1){
    ${targetName}Index = source.size${ctargetName}s();
  }
</#if>

<#if isSourceList>
  target.add${csourceName}(${targetName}Index, source, NotificationScope.MINIMAL);
<#else>
  target.set${csourceName}(source, NotificationScope.MINIMAL);
</#if>

<#if isTargetList>
  source.add${ctargetName}(${sourceName}Index, target, NotificationScope.MINIMAL);
<#else>
  source.set${ctargetName}(target, NotificationScope.MINIMAL);
</#if>

}else{
  // Do not trigger load of ${csourceName}. If the ${ctargetName} is loaded, we can change it instead and avoid a load operation
  if(loaded${ctargetName}){
    ${targetType} target = ${definition}Manager.get${targetType}Sure(${targetName}GemId);
<#if isSourceList>
    if(${targetName}Index == -1){
      ${targetName}Index = target.size${csourceName}s();
    }
    target.INTERNAL_add${csourceName}Id(${targetName}Index, ${sourceName}GemId, NotificationScope.MINIMAL);
<#else>
   target._internal_set${csourceName}(${sourceName}GemId, NotificationScope.MINIMAL);
</#if>
} else if(loaded${csourceName}) {
    ${sourceType} source = ${definition}Manager.get${sourceType}Sure(${sourceName}GemId);
<#if isTargetList>
    if(${sourceName}Index == -1){
      ${sourceName}Index = source.size${ctargetName}s();
    }
    source.INTERNAL_add${ctargetName}Id(${sourceName}Index, ${targetName}GemId, NotificationScope.MINIMAL);
<#else>
    source.INTERNAL_set${ctargetName}Id(${targetName}GemId, NotificationScope.MINIMAL);
</#if>
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
}

return StatusResult.ok(getId());