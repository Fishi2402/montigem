<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass", "attributes")}
${cd4c.method("public ${domainClass}ValidationResult shallowClone()")}
let shallowClone = new ${domainClass}ValidationResult();
if(this.hasGemId()) {
  shallowClone.setGemId(this.getGemId());
}
<#list attributes as attribute>
  this.${attribute}ValidationMessages.forEach((message: any) => {
    shallowClone.add${attribute?cap_first}ValidationMessage(ValidationMessage.create(message.getStatus(), message.getMessage()));
  });
</#list>
return shallowClone;
