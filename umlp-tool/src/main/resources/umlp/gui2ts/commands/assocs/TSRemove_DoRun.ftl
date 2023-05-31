<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName","sourceType","targetType",
                "isSourceList","isTargetList","definition")}
${cd4c.method("public StatusResult doRun()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>

let loaded${ctargetName}: boolean = ${definition}Manager.isLoaded${targetType}(this.${targetName}GemId);
let loaded${csourceName}: boolean = ${definition}Manager.isLoaded${sourceType}(this.${sourceName}GemId);

// TODO: With DB: server must have loaded both
console.assert(CommandManager.getParticipantId() != 0 || (loaded${ctargetName} && loaded${csourceName}), "0x50008 Remove. source: ${sourceName}, target: ${targetName}");

if(loaded${ctargetName} && loaded${csourceName}){
  let p: ${sourceType} = ${definition}Manager.get${sourceType}Sure(this.${sourceName}GemId);
  let a: ${targetType} = ${definition}Manager.get${targetType}Sure(this.${targetName}GemId);

  // TODO: only in server?
<#if isTargetList>
  if(this.${sourceName}Index == -1){
    this.${sourceName}Index = p._internal_get${ctargetName}GemList().indexOfId(this.${targetName}GemId);
  }
  p.remove${ctargetName}1(this.${sourceName}Index, NotificationScope.MINIMAL);
<#else>
  // TODO
</#if>

<#if isSourceList>
  if(this.${targetName}Index == -1){
    this.${targetName}Index = a._internal_get${csourceName}GemList().indexOfId(this.${sourceName}GemId);
  }
  a.remove${csourceName}1(this.${targetName}Index, NotificationScope.MINIMAL);
<#else>
  // TODO
</#if>

}else{
  // Do not trigger load of ${sourceName}. If the ${targetName} is loaded, we can change it instead and avoid a load operation
  if(loaded${ctargetName}){
    let a: ${targetType} = ${definition}Manager.get${targetType}Sure(this.${targetName}GemId);
<#if isSourceList>
    if(this.${targetName}Index == -1){
      this.${targetName}Index = a._internal_get${csourceName}GemList().indexOfId(this.${sourceName}GemId);
    }
    a.remove${csourceName}1(this.${targetName}Index, NotificationScope.MINIMAL);
<#else>
    // TODO
</#if>
  } else if(loaded${csourceName}) {
    let p: ${sourceType} = ${definition}Manager.get${sourceType}Sure(this.${sourceName}GemId);
<#if isTargetList>
    if(this.${sourceName}Index == -1){
      this.${sourceName}Index = p._internal_get${ctargetName}GemList().indexOfId(this.${targetName}GemId);
    }
    p.remove${ctargetName}1(this.${sourceName}Index, NotificationScope.MINIMAL);
<#else>
  // TODO
</#if>
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(this.getId());
  }
}

return StatusResult.ok(this.getId());