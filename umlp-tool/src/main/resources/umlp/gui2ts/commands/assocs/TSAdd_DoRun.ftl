<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assoc","sourceName","targetName","sourceType","targetType",
               "isSourceList","isTargetList","definition")}
${cd4c.method("public StatusResult doRun()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>
let loaded${ctargetName}: boolean = ${definition}Manager.isLoaded${targetType}(this.${targetName}GemId);
let loaded${csourceName}: boolean = ${definition}Manager.isLoaded${sourceType}(this.${sourceName}GemId);

// TODO: With DB: server must have loaded both
console.assert(CommandManager.getParticipantId() != 0 || (loaded${ctargetName} && loaded${csourceName}), "0x50008 Add. source: ${sourceName}, target: ${targetName}");

if(loaded${ctargetName} && loaded${csourceName}){
  let source: ${sourceType} = ${definition}Manager.get${sourceType}Sure(${sourceName}GemId);
  let target: ${targetType} = ${definition}Manager.get${targetType}Sure(${targetName}GemId);

  // TODO: only in server?
<#if isSourceList>
  if(this.${sourceName}Index == -1){
    this.${sourceName}Index = target.size${csourceName}s();
  }
</#if>

<#if isTargetList>
  if(this.${targetName}Index == -1){
    this.${targetName}Index = source.size${ctargetName}s();
  }
</#if>

<#if isSourceList>
  target.add${csourceName}(this.${targetName}Index, source, NotificationScope.MINIMAL);
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
  if(this.loaded${ctargetName}){
    let target: ${targetType} = ${definition}Manager.get${targetType}Sure(this.${targetName}GemId);
<#if isSourceList>
    if(this.${targetName}Index == -1){
      this.${targetName}Index = target.size${csourceName}s();
    }
    target.INTERNAL_add${csourceName}Id(this.${targetName}Index, this.${sourceName}GemId, NotificationScope.MINIMAL);
<#else>
   target._internal_set${csourceName}(this.${sourceName}GemId, NotificationScope.MINIMAL);
</#if>
} else if(this.loaded${csourceName}) {
    let source: ${sourceType} = ${definition}Manager.get${sourceType}Sure(${sourceName}GemId);
<#if isTargetList>
    if(this.${sourceName}Index == -1){
      this.${sourceName}Index = source.size${ctargetName}s();
    }
    source.INTERNAL_add${ctargetName}Id(this.${sourceName}Index, this.${targetName}GemId, NotificationScope.MINIMAL);
<#else>
    source._internal_set${ctargetName}(this.${targetName}GemId, NotificationScope.MINIMAL);
</#if>
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(this.getId());
  }
}

return StatusResult.ok(this.getId());