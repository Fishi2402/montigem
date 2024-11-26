<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "managerName", "packagename", "hasOCL")}
${cd4c.method("public StatusResult doAction()")}

<#if hasOCL>
  ${classname}Validator validator = new ${classname}Validator();
  ${classname}ValidationResult validationResult = validator.validate(${classname?uncap_first});
  if (validationResult.hasErrors()) {
    return new ValidationResult(getId(), validationResult, "sehub.${classname}ValidationResult");
  }
</#if>
${managerName}.addWithScope${classname}(${classname?uncap_first}, NotificationScope.LOCAL);
return StatusResult.ok(getId());
