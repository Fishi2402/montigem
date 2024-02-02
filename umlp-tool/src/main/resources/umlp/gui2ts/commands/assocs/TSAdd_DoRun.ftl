<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assoc","sourceName","targetName","sourceType","targetType",
               "isSourceList","isTargetList","manager", "hasOCL")}
${cd4c.method("public Promise<StatusResult> doRun()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>
let loaded${ctargetName}: boolean = ${manager}.isLoaded${targetType}(this.${targetName}GemId);
let loaded${csourceName}: boolean = ${manager}.isLoaded${sourceType}(this.${sourceName}GemId);

// TODO: With DB: server must have loaded both
console.assert(CommandManager.getParticipantId() != 0 || (loaded${ctargetName} && loaded${csourceName}), "0x50008 Add. source: ${sourceName}, target: ${targetName}");

let promiseSource = ${manager}.get${sourceType}Sure(this.${sourceName}GemId);
let promiseTarget = ${manager}.get${targetType}Sure(this.${targetName}GemId);

let promiseRes;

if(loaded${ctargetName} && loaded${csourceName}){
  promiseRes = Promise.all([
    promiseSource,
    promiseTarget
  ]).then(v => {
    let source: ${sourceType} = v[0];
    let target: ${targetType} = v[1];

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
    target.add${csourceName}1(this.${targetName}Index, source, NotificationScope.MINIMAL);
  <#else>
    target.set${csourceName}(source, NotificationScope.MINIMAL);
  </#if>

  <#if isTargetList>
    source.add${ctargetName}1(this.${sourceName}Index, target, NotificationScope.MINIMAL);
  <#else>
    source.set${ctargetName}(target, NotificationScope.MINIMAL);
  </#if>
  });

} else {
  // Do not trigger load of ${csourceName}. If the ${ctargetName} is loaded, we can change it instead and avoid a load operation
  if (loaded${ctargetName}) {
    promiseRes = promiseTarget.then(v => {
      let target: ${targetType} = v;
    <#if isSourceList>
      if(this.${targetName}Index == -1){
        this.${targetName}Index = target.size${csourceName}s();
      }
      target.INTERNAL_add${csourceName}Id(this.${targetName}Index, this.${sourceName}GemId, NotificationScope.MINIMAL);
    <#else>
      target._internal_set${csourceName}(this.${sourceName}GemId, NotificationScope.MINIMAL);
    </#if>
    });
  } else if(loaded${csourceName}) {
    promiseRes = promiseSource.then(v => {
      let source: ${sourceType} = v;
    <#if isTargetList>
      if(this.${sourceName}Index == -1){
        this.${sourceName}Index = source.size${ctargetName}s();
      }
      source.INTERNAL_add${ctargetName}Id(this.${sourceName}Index, this.${targetName}GemId, NotificationScope.MINIMAL);
    <#else>
      source._internal_set${ctargetName}(this.${targetName}GemId, NotificationScope.MINIMAL);
    </#if>
    });
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return Promise.resolve(StatusResult.error(this.getId()));
  }
}

return promiseRes.then(() => StatusResult.ok(this.getId()));
