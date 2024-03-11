
<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes", "assocTypes", "assocs", "listAssocTypes", "listAssocs")}
${cd4c.method("public ${className}ValidationResult deserialize(any json)")}
let result = new ${className}ValidationResult();
<#list attributes as attr>
  for(let o of json["${attr}Messages"]) {
    let message = new ValidationMessageDeSer().deserialize(o);
    if(message !== undefined) {
      result.add${attr?cap_first}ValidationMessage(message);
    }
  }
</#list>
<#list assocTypes as assocType>
  if(json["${assocs[assocType_index]}ValidationResult"] !== undefined) {
    let ${assocs[assocType_index]}ValidationResult = new ${assocType}ValidationResultDeSer().deserialize(json["${assocs[assocType_index]}ValidationResult"]);
    if(${assocs[assocType_index]}ValidationResult !== undefined) {
      result.set${assocs[assocType_index]?cap_first}ValidationResult(${assocs[assocType_index]}ValidationResult);
    }
  }
</#list>
<#list listAssocTypes as listAssocType>
  if(json["${listAssocs[listAssocType_index]}ValidationResults"] !== undefined) {
    for(let o of json["${listAssocs[listAssocType_index]}ValidationResults"]) {
      let ${listAssocs[listAssocType_index]}ValidationResult = new ${listAssocType}ValidationResultDeSer().deserialize(o);
      if(${listAssocs[listAssocType_index]}ValidationResult !== undefined) {
        result.add${listAssocs[listAssocType_index]?cap_first}ValidationResult(${listAssocs[listAssocType_index]}ValidationResult);
      }
    }
  }
</#list>
return result;
