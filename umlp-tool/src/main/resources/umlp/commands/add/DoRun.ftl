<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","definition", "isGem", "targetname", "targettype")}
${cd4c.method("public StatusResult doRun()")}

Optional<${classname}> ${classname?uncap_first}Opt = ${definition}Manager.${classname?uncap_first}Opt(gemId);
if(${classname?uncap_first}Opt.isEmpty()){
  // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}
<#if isGem>
  Optional<${targettype}> ${targetname}Opt = ${definition}Manager.${targettype?uncap_first}Opt(${targetname}GemId);
  if (${targetname}Opt.isEmpty()) {
    // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
  ${classname?uncap_first}Opt.get().add${targetname?cap_first}(indexInList, ${targetname}Opt.get(), NotificationScope.LOCAL);
<#else>
  // TODO
</#if>
return StatusResult.error(getId());