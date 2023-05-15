<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName", "sourceType", "targetType",
                "isSourceList","isTargetList","definition")}
<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>

${cd4c.method("protected StatusResult exchange${ctargetName}()")}

let ${sourceName}Loaded: boolean = ${definition}Manager.isLoaded${sourceType}(this.${sourceName}GemId);
let old${ctargetName}Loaded: boolean = ${definition}Manager.isLoaded${targetType}(this.old${ctargetName}GemId);
let new${ctargetName}Loaded: boolean = ${definition}Manager.isLoaded${targetType}(this.${targetName}GemId);

if(${sourceName}Loaded){
  let p: ${sourceType} = ${definition}Manager.get${sourceType}Sure(this.${sourceName}GemId);
  // Will automatically change other side of the association
  <#if isTargetList>
    p._internal_set${ctargetName}(this.${targetName}Index, this.${targetName}GemId, NotificationScope.LOCAL);
  <#else>
    p._internal_set${ctargetName}(this.${targetName}GemId, NotificationScope.LOCAL);
  </#if>
} else if(old${ctargetName}Loaded){
  let old${ctargetName}: ${targetType} = ${definition}Manager.get${targetType}Sure(this.old${ctargetName}GemId);
  // Does not change other side
<#if isSourceList>
  old${ctargetName}.remove${csourceName}(this.${targetName}Index, NotificationScope.MINIMAL);
<#else>
  // Do nothing?
</#if>
} else {
  return StatusResult.error(this.getId());
}

if(new${ctargetName}Loaded){
  let newLoaded: ${targetType} = ${definition}Manager.get${targetType}Sure(${targetName}GemId);
<#if isSourceList>
  newLoaded._internal_add${csourceName}(newLoaded.size${csourceName}s(), Lists.newArrayList(this.${sourceName}GemId), NotificationScope.MINIMAL);
<#else>
  newLoaded._internal_set${csourceName}(this.${sourceName}GemId, NotificationScope.MINIMAL);
</#if>}

return StatusResult.ok(this.getId());