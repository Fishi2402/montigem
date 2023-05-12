<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","definition", "isGem", "targetname")}
${cd4c.method("public StatusResult doRun()")}

let ${classname?uncap_first}Opt: ${classname} = ${definition}Manager.${classname?uncap_first}Opt(this.gemId);
if(${classname?uncap_first}Opt == null){
  // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(this.getId());
}
<#if isGem>
  ${classname?uncap_first}Opt.remove${targetname?cap_first}ByIdSure(this.${targetname}GemId);
<#else>
  // TODO
</#if>
return StatusResult.error(this.getId());