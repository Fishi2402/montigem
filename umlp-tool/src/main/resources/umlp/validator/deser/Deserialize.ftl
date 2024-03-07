
<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes", "assocTypes", "assocs", "listAssocTypes", "listAssocs")}
${cd4c.method("public Optional<${className}ValidationResult> deserialize(JsonObject jsonObject)")}
${className}ValidationResult result = new ${className}ValidationResult();
<#list attributes as attr>
  for(var o : jsonObject.getArrayMember("${attr}Messages")) {
    Optional<ValidationMessage> message = new ValidationMessageDeSer().deserialize(o.getAsJsonObject());
    message.ifPresent(result::add${attr?cap_first}ValidationMessage);
  }
</#list>
<#list assocTypes as assocType>
  if(jsonObject.hasMember("${assocs[assocType_index]}ValidationResult")) {
    Optional<${assocType}ValidationResult> ${assocs[assocType_index]}ValidationResult = new ${assocType}ValidationResultDeSer().deserialize(jsonObject.getObjectMember("${assocs[assocType_index]}ValidationResult"));
    ${assocs[assocType_index]}ValidationResult.ifPresent(result::set${assocs[assocType_index]?cap_first}ValidationResult);
  }
</#list>
<#list listAssocTypes as listAssocType>
  if(jsonObject.hasMember("${listAssocs[listAssocType_index]}ValidationResults")) {
    for(var o : jsonObject.getArrayMember("${listAssocs[listAssocType_index]}ValidationResults")) {
      Optional<${listAssocType}ValidationResult> ${listAssocs[listAssocType_index]}ValidationResult = new ${listAssocType}ValidationResultDeSer().deserialize(o.getAsJsonObject());
      ${listAssocs[listAssocType_index]}ValidationResult.ifPresent(result::add${listAssocs[listAssocType_index]?cap_first}ValidationResult);
    }
  }
</#list>
return Optional.of(result);
