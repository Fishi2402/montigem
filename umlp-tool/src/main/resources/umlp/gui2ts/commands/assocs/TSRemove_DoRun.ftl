<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName","sourceType","targetType",
                "isSourceList","isTargetList","manager", "hasOCL")}
${cd4c.method("public Promise<StatusResult> doRun()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>

let loaded${ctargetName}: boolean = ${manager}.isLoaded${targetType}(this.${targetName}GemId);
let loaded${csourceName}: boolean = ${manager}.isLoaded${sourceType}(this.${sourceName}GemId);

// TODO: With DB: server must have loaded both
console.assert(CommandManager.getParticipantId() != 0 || (loaded${ctargetName} && loaded${csourceName}), "0x50008 Remove. source: ${sourceName}, target: ${targetName}");

let promiseSource = ${manager}.get${sourceType}Sure(this.${sourceName}GemId);
let promiseTarget = ${manager}.get${targetType}Sure(this.${targetName}GemId);

let promiseRes;

if(loaded${ctargetName} && loaded${csourceName}){
  promiseRes = Promise.all([
    promiseSource,
    promiseTarget
  ]).then(v => {
    let p: ${sourceType} = v[0];
    let a: ${targetType} = v[1];

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
  });

}else{
  // Do not trigger load of ${sourceName}. If the ${targetName} is loaded, we can change it instead and avoid a load operation
  if(loaded${ctargetName}){
    promiseRes = promiseTarget.then(v => {
      let a: ${targetType} = v;
    <#if isSourceList>
      if(this.${targetName}Index == -1){
        this.${targetName}Index = a._internal_get${csourceName}GemList().indexOfId(this.${sourceName}GemId);
      }
      a.remove${csourceName}1(this.${targetName}Index, NotificationScope.MINIMAL);
    <#else>
      // TODO
    </#if>
    });
  } else if(loaded${csourceName}) {
    promiseRes = promiseSource.then(v => {
      let p: ${sourceType} = v;
    <#if isTargetList>
      if(this.${sourceName}Index == -1){
        this.${sourceName}Index = p._internal_get${ctargetName}GemList().indexOfId(this.${targetName}GemId);
      }
      p.remove${ctargetName}1(this.${sourceName}Index, NotificationScope.MINIMAL);
    <#else>
      // TODO
    </#if>
    });
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return Promise.resolve(StatusResult.error(this.getId()));
  }
}

return promiseRes.then(() => StatusResult.ok(this.getId()));
