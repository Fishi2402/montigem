<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName", "sourceType", "targetType",
                "isSourceList","isTargetList","definition")}
<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>

${cd4c.method("protected StatusResult exchange${ctargetName}()")}

boolean ${sourceName}Loaded = ${definition}Manager.isLoaded${sourceType}(${sourceName}GemId);
boolean old${ctargetName}Loaded = ${definition}Manager.isLoaded${targetType}(old${ctargetName}GemId);
boolean new${ctargetName}Loaded = ${definition}Manager.isLoaded${targetType}(${targetName}GemId);

if(${sourceName}Loaded){
  ${sourceType} p = ${definition}Manager.get${sourceType}Sure(${sourceName}GemId);
  // Will automatically change other side of the association
  p._internal_set${ctargetName}(${sourceName}Index, ${targetName}GemId, NotificationScope.LOCAL);
} else if(old${ctargetName}Loaded){
  ${targetType} old${ctargetName} = ${definition}Manager.get${targetType}Sure(old${ctargetName}GemId);
  // Does not change other side
<#if isSourceList>
  old${ctargetName}.remove${csourceName}(${targetName}Index, NotificationScope.MINIMAL);
<#else>
  // TODO: old${ctargetName}.remove${csourceName}(${targetName}Index, NotificationScope.MINIMAL);
</#if>
} else {
  return StatusResult.error(getId());
}

if(new${ctargetName}Loaded){
  ${targetType} newLoaded = ${definition}Manager.get${targetType}Sure(${targetName}GemId);
<#if isSourceList>
  newLoaded._internal_add${csourceName}(newLoaded.size${csourceName}s(), Lists.newArrayList(${sourceName}GemId), NotificationScope.MINIMAL);
<#else>
  // TODO: newLoaded._internal_add${csourceName}(newLoaded.size${csourceName}s(), Lists.newArrayList(${sourceName}GemId), NotificationScope.MINIMAL);
</#if>}

return StatusResult.ok(getId());