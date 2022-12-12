<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","definition", "isGem", "targetname")}
${cd4c.method("public StatusResult doRun()")}

Optional<${classname}> ${classname?uncap_first}Opt = ${definition}Manager.${classname?uncap_first}Opt(gemId);
if(${classname?uncap_first}Opt.isEmpty()){
  // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}
<#if isGem>
  ${classname?uncap_first}Opt.get().remove${targetname?cap_first}ByIdSure(${targetname}GemId);
<#else>
  // TODO
</#if>
return StatusResult.error(getId());