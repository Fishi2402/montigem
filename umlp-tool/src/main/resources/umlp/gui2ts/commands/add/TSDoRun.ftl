<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","manager", "isGem", "targetname", "targettype", "targetTypeManager", "hasOCL")}
${cd4c.method("public Promise<StatusResult> doRun()")}
return Promise.all([
  ${manager}.${classname?uncap_first}Opt(this.gemId)
  <#if isGem>, ${targetTypeManager}.${targettype?uncap_first}Opt(this.${targetname}GemId)</#if>
]).then(v => {
  let ${classname?uncap_first}Opt = v[0];
  if(${classname?uncap_first}Opt == null){
    // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(this.getId());
  }
  <#if isGem>
    let ${targetname}Opt: ${targettype} = v[1];
    if (${targetname}Opt == null) {
      // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(this.getId());
    }
    ${classname?uncap_first}Opt.add${targetname?cap_first}1(this.indexInList, ${targetname}Opt, NotificationScope.LOCAL);
  <#else>
    // TODO
  </#if>
  return StatusResult.ok(this.getId());
});
