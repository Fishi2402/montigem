<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName", "sourceType", "targetType",
                "isSourceList","isTargetList","managerName", "hasOCL")}
<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>

${cd4c.method("protected StatusResult exchange${ctargetName}()")}

boolean ${sourceName}Loaded = ${managerName}.isLoaded${sourceType}(${sourceName}GemId);
boolean old${ctargetName}Loaded = ${managerName}.isLoaded${targetType}(old${ctargetName}GemId);
boolean new${ctargetName}Loaded = ${managerName}.isLoaded${targetType}(${targetName}GemId);

if(${sourceName}Loaded){
  ${sourceType} p = ${managerName}.get${sourceType}Sure(${sourceName}GemId);
  // Will automatically change other side of the association
  <#if isTargetList>
    p._internal_set${ctargetName}(${targetName}Index, ${targetName}GemId, NotificationScope.LOCAL);
  <#else>
    p._internal_set${ctargetName}(${targetName}GemId, NotificationScope.LOCAL);
  </#if>
} else if(old${ctargetName}Loaded){
  ${targetType} old${ctargetName} = ${managerName}.get${targetType}Sure(old${ctargetName}GemId);
  // Does not change other side
<#if isSourceList>
  old${ctargetName}.remove${csourceName}(${targetName}Index, NotificationScope.MINIMAL);
<#else>
  // Do nothing?
</#if>
} else if(new${ctargetName}Loaded){
  // The target is already loaded -> update bidirectional (using local)
  // ToDo: Decide if we want to load ${sourceName}GemId (lazy loading) or just keep track of the id
  ${targetType} newLoaded = ${managerName}.get${targetType}Sure(${targetName}GemId);
<#if isSourceList>
  newLoaded._internal_add${csourceName}(newLoaded.size${csourceName}s(), Lists.newArrayList(${sourceName}GemId), NotificationScope.LOCAL);
<#else>
  newLoaded._internal_set${csourceName}(${sourceName}GemId, NotificationScope.LOCAL);
</#if>
}
else {
return StatusResult.error(getId());
}
return StatusResult.ok(getId());
