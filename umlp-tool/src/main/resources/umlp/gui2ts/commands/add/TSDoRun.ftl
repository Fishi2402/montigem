<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","definition", "isGem", "targetname", "targettype")}
${cd4c.method("public StatusResult doRun()")}

let ${classname?uncap_first}Opt: ${classname} = ${definition}Manager.${classname?uncap_first}Opt(gemId);
if(${classname?uncap_first}Opt == null){
  // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}
<#if isGem>
  let ${targetname}Opt: ${targettype} = ${definition}Manager.${targettype?uncap_first}Opt(${targetname}GemId);
  if (${targetname}Opt.isEmpty()) {
    // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(getId());
  }
  ${classname?uncap_first}Opt.add${targetname?cap_first}(this.indexInList, ${targetname}Opt, NotificationScope.LOCAL);
<#else>
  // TODO
</#if>
return StatusResult.error(this.getId());