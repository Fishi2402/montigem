<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass", "attributes")}
${cd4c.method("public ${domainClass}ValidationResult shallowClone()")}
${domainClass}ValidationResult shallowClone = new ${domainClass}ValidationResult();
if(this.hasGemId()) {
  shallowClone.setGemId(this.getGemId());
}
<#list attributes as attribute>
  this.${attribute}ValidationMessages.forEach(message -> shallowClone.add${attribute?cap_first}ValidationMessage(new ValidationMessage(message.getStatus(), message.getMessage())));
</#list>
return shallowClone;
