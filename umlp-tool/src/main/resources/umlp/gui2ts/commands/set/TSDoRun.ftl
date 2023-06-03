<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","definition", "isGem", "targettype")}
${cd4c.method("public Promise<StatusResult> doRun()")}

console.debug("Executing set_${attributename} to " + this.${attributename}<#if isGem>GemId</#if> + " on ${classname?uncap_first} " + this.gemId);
console.trace();
return Promise.all([
  ${definition}Manager.${classname?uncap_first}Opt(this.gemId)
  <#if isGem>, ${definition}Manager.create${targettype}Gem(this.${attributename}GemId)</#if>
]).then(v => {
  let ${classname?uncap_first}Opt = v[0];
  if(${classname?uncap_first}Opt != null) {
  <#if isGem>
    ${classname?uncap_first}Opt.set${attributename?cap_first}Gem(v[1], NotificationScope.LOCAL);
  <#else>
    ${classname?uncap_first}Opt.set${attributename?cap_first}(this.${attributename}, NotificationScope.LOCAL);
  </#if>
    return StatusResult.ok(this.getId());
  } else {
    GeneralErrorCollector.reportSyncError();
    return StatusResult.error(this.getId());
  }
});