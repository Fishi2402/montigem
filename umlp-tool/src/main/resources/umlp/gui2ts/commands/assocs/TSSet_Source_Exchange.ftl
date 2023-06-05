<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName", "sourceType", "targetType",
                "isSourceList","isTargetList","definition")}
<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>

${cd4c.method("protected Promise<StatusResult> exchange${ctargetName}()")}

let ${sourceName}Loaded: boolean = ${definition}Manager.isLoaded${sourceType}(this.${sourceName}GemId);
let old${ctargetName}Loaded: boolean = ${definition}Manager.isLoaded${targetType}(this.old${ctargetName}GemId);
let new${ctargetName}Loaded: boolean = ${definition}Manager.isLoaded${targetType}(this.${targetName}GemId);

let promiseRes1 = Promise.resolve();

if(${sourceName}Loaded){
  promiseRes1 = ${definition}Manager.get${sourceType}Sure(this.${sourceName}GemId).then(v => {
    let p: ${sourceType} = v;
    // Will automatically change other side of the association
    <#if isTargetList>
      p._internal_set${ctargetName}(this.${targetName}Index, this.${targetName}GemId, NotificationScope.LOCAL);
    <#else>
      p._internal_set${ctargetName}(this.${targetName}GemId, NotificationScope.LOCAL);
    </#if>
  });
} else if(old${ctargetName}Loaded){
  promiseRes1 = ${definition}Manager.get${targetType}Sure(this.old${ctargetName}GemId).then(v => {
    let old${ctargetName}: ${targetType} = v;
    // Does not change other side
  <#if isSourceList>
    old${ctargetName}.remove${csourceName}1(this.${targetName}Index, NotificationScope.MINIMAL);
  <#else>
    // Do nothing?
  </#if>
  });
} else {
  return Promise.resolve(StatusResult.error(this.getId()));
}

let promiseRes2 = Promise.resolve();

if(new${ctargetName}Loaded){
  promiseRes2 = ${definition}Manager.get${targetType}Sure(this.${targetName}GemId).then(v => {
    let newLoaded: ${targetType} = v
  <#if isSourceList>
    newLoaded._internal_add${csourceName}(newLoaded.size${csourceName}s(), [this.${sourceName}GemId], NotificationScope.MINIMAL);
  <#else>
    newLoaded._internal_set${csourceName}(this.${sourceName}GemId, NotificationScope.MINIMAL);
  </#if>
  });
}

return Promise.all([promiseRes1, promiseRes2]).then(() => StatusResult.ok(this.getId()));