<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes", "assocTypes", "assocs", "listAssocTypes", "listAssocs")}
${cd4c.method("public void buildJson(${className}ValidationResult obj, JsonPrinter jsonPrinter)")}
<#list attributes as attr>
  jsonPrinter.beginArray("${attr}Messages");
  for(ValidationMessage msg : obj.get${attr?cap_first}ValidationMessages()) {
    new ValidationMessageDeSer().buildJson(msg, jsonPrinter);
  }
  jsonPrinter.endArray();
</#list>
<#list assocTypes as assocType>
  if(obj.has${assocs[assocType_index]?cap_first}ValidationResult()) {;
    jsonPrinter.memberJson("${assocs[assocType_index]}ValidationResult", new ${assocType}ValidationResultDeSer().serialize(obj.get${assocs[assocType_index]?cap_first}ValidationResult()));
  }
</#list>
<#list listAssocTypes as listAssocType>
  jsonPrinter.beginArray("${listAssocType}ValidationResults");
  for(${listAssocType}ValidationResult object : obj.get${listAssocs[listAssocType_index]?cap_first}ValidationResults()) {
    jsonPrinter.memberJson("listAssocs[listAssocType_index]ValidationResult", new ${listAssocType}ValidationResultDeSer().serialize(object));
  }
  jsonPrinter.endArray();
</#list>
