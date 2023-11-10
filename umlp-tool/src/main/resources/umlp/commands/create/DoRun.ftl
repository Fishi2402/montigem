<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "managerName", "packagename", "hasOCL")}
${cd4c.method("public StatusResult doAction()")}

<#if hasOCL>
${classname}Validator validator = new ${classname}Validator();
List<ValidationMessage> messages = validator.validate(${classname?uncap_first});
if (messages.stream().anyMatch(m -> m.getStatus() == ValidationStatus.ERROR)) {
  Log.error("Validation failed for ${classname?uncap_first}:\n" + messages.stream().filter(m -> m.getStatus() == ValidationStatus.ERROR).map(ValidationMessage::getMessage).collect(Collectors.joining("\n")));
  return StatusResult.error(this.getId());
}
</#if>
${managerName}.add${classname}(${classname?uncap_first}, NotificationScope.LOCAL);
return StatusResult.ok(getId());
