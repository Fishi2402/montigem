<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourceName","targetName","sourceType","targetType",
                "isSourceList","isTargetList","managerName", "hasOCL")}
${cd4c.method("public StatusResult doAction()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>

boolean loaded${ctargetName} = ${managerName}.isLoaded${targetType}(${targetName}GemId);
boolean loaded${csourceName} = ${managerName}.isLoaded${sourceType}(${sourceName}GemId);

if(loaded${ctargetName}){
  ${targetType} a = ${managerName}.get${targetType}Sure(${targetName}GemId);
<#if isSourceList>
  // TODO: Why do we use ${targetName}(targetName) instead of ${sourceName}(sourceName) here?
  if(${sourceName}Index == -1){
    ${sourceName}Index = a._internal_get${csourceName}GemList().indexOfId(${sourceName}GemId);
  }
  a.remove${csourceName}(${sourceName}Index, NotificationScope.LOCAL);
<#else>
a._internal_set${csourceName}(-1, NotificationScope.LOCAL);
</#if>
} else if(loaded${csourceName}) {
  ${sourceType} p = ${managerName}.get${sourceType}Sure(${sourceName}GemId);
<#if isTargetList>
  if(${targetName}Index == -1){
    ${targetName}Index = p._internal_get${ctargetName}GemList().indexOfId(${targetName}GemId);
  }
  p.remove${ctargetName}(${sourceName}Index, NotificationScope.LOCAL);
<#else>
p._internal_set${ctargetName}(-1, NotificationScope.LOCAL);
</#if>
} else {
  // Why did the client receive this command, if it does not contain a or p?
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}

return StatusResult.ok(getId());
