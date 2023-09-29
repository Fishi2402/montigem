<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assoc","sourceName","targetName","sourceType","targetType",
               "isSourceList","isTargetList","managerName")}
${cd4c.method("public StatusResult doAction()")}

<#assign csourceName=sourceName?cap_first>
<#assign ctargetName=targetName?cap_first>
boolean loaded${ctargetName} = ${managerName}.isLoaded${targetType}(${targetName}GemId);
boolean loaded${csourceName} = ${managerName}.isLoaded${sourceType}(${sourceName}GemId);

if(loaded${ctargetName} && loaded${csourceName}){
  ${sourceType} source = ${managerName}.get${sourceType}Sure(${sourceName}GemId);
  ${targetType} target = ${managerName}.get${targetType}Sure(${targetName}GemId);

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
  target.add${csourceName}(${sourceName}Index, source, NotificationScope.MINIMAL);
<#else>
  target.set${csourceName}(source, NotificationScope.MINIMAL);
</#if>

<#if isTargetList>
  source.add${ctargetName}(${targetName}Index, target, NotificationScope.MINIMAL);
<#else>
  source.set${ctargetName}(target, NotificationScope.MINIMAL);
</#if>

}else{
  // Do not trigger load of ${csourceName}. If the ${ctargetName} is loaded, we can change it instead and avoid a load operation
  if(loaded${ctargetName}){
    ${targetType} target = ${managerName}.get${targetType}Sure(${targetName}GemId);
<#if isSourceList>
    if(${targetName}Index == -1){
      ${targetName}Index = target.size${csourceName}s();
    }
    target.INTERNAL_add${csourceName}Id(${targetName}Index, ${sourceName}GemId, NotificationScope.MINIMAL);
<#else>
   target._internal_set${csourceName}(${sourceName}GemId, NotificationScope.MINIMAL);
</#if>
} else if(loaded${csourceName}) {
    ${sourceType} source = ${managerName}.get${sourceType}Sure(${sourceName}GemId);
<#if isTargetList>
    if(${sourceName}Index == -1){
      ${sourceName}Index = source.size${ctargetName}s();
    }
    source.INTERNAL_add${ctargetName}Id(${sourceName}Index, ${targetName}GemId, NotificationScope.MINIMAL);
<#else>
    source._internal_set${ctargetName}(${targetName}GemId, NotificationScope.MINIMAL);
</#if>
  } else {
    // Why did the client receive this command, if it does not contain a or p?
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
}

return StatusResult.ok(getId());
