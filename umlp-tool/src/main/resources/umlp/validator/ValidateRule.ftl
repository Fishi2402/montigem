<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oclRuleName", "oclRule", "oclMessage", "inputParams", "attributes")}
${cd4c.method("public ValidationMessage validate${oclRuleName}(${inputParams})")}
  boolean oclRule = ${oclRule};
  if(!oclRule) {
    return new ValidationMessage(ValidationStatus.ERROR, "${oclMessage}", Arrays.asList(<#list attributes as attribute>"${attribute}"<#if attribute_has_next>, </#if></#list>));
  }

  return new ValidationMessage(ValidationStatus.OK, "", Arrays.asList(<#list attributes as attribute>"${attribute}"<#if attribute_has_next>, </#if></#list>));