<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","definition", "isGem", "targetname", "targettype")}
${cd4c.method("public Promise<StatusResult> doRun()")}
return Promise.all([
  ${definition}Manager.${classname?uncap_first}Opt(this.gemId)
  <#if isGem>, ${definition}Manager.${targettype?uncap_first}Opt(this.${targetname}GemId)</#if>
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
