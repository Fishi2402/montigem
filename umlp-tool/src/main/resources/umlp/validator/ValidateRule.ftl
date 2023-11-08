<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oclRuleName", "oclRule", "oclMessage", "inputParams")}
${cd4c.method("public ValidationMessage validate${oclRuleName}(${inputParams})")}
  boolean oclRule = ${oclRule};
  if(!oclRule) {
    return new ValidationMessage(ValidationStatus.ERROR, "${oclMessage}");
  }

  return new ValidationMessage(ValidationStatus.OK, "");