<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","managerName", "isGem", "targettype", "targetManagerType", "packagename")}
${cd4c.method("public StatusResult doAction()")}

Log.trace("Executing set_${attributename} to " + ${attributename}<#if isGem>GemId</#if> + " on ${classname?uncap_first} " + gemId, LogUtil.getLoggerName());
Optional<${classname}> ${classname?uncap_first}Opt = ${managerName}.${classname?uncap_first}Opt(gemId);
if(${classname?uncap_first}Opt.isPresent()){
<#if isGem>
    ${classname?uncap_first}Opt.get().set${attributename?cap_first}Gem(${targetManagerType}.create${targettype}Gem(${attributename}GemId), NotificationScope.LOCAL);
<#else>
    ${classname?uncap_first}Opt.get().set${attributename?cap_first}(${attributename}, NotificationScope.LOCAL);
</#if>
    ${classname}Validator validator = new ${classname}Validator();
    ${classname}ValidationResult validationResult = validator.validate(${classname?uncap_first}Opt.get());
    if (validationResult.hasErrors()) {
    return new ValidationResult(getId(), validationResult, "sehub.${classname}ValidationResult");
    }
    return StatusResult.ok(getId());
} else {
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}
