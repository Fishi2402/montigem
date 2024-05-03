<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","managerName", "isGem", "targetname", "hasOCL")}
${cd4c.method("public StatusResult doAction()")}

Optional<${classname}> ${classname?uncap_first}Opt = ${managerName}.${classname?uncap_first}Opt(gemId);
if(${classname?uncap_first}Opt.isEmpty()){
  // TODO AHe: add GemId to reportSyncError or replace with <Class>ErrorCollector.reportPersonNotPresent(gemId)...
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}
<#if isGem>
  ${classname?uncap_first}Opt.get().remove${targetname?cap_first}ByIdSure(${targetname}GemId);
<#else>
  ${classname?uncap_first}Opt.get().remove${attributename?cap_first}(indexInList, NotificationScope.LOCAL);
</#if>
<#if hasOCL>
  ${classname}Validator validator = new ${classname}Validator();
  ${classname}ValidationResult validationResult = validator.validate(${classname?uncap_first}Opt.get());
  if (validationResult.hasErrors()) {
    return new ValidationResult(getId(), validationResult, "sehub.${classname}ValidationResult");
  }
</#if>
return StatusResult.error(getId());
