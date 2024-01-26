<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes", "assocTypes", "assocs", "listAssocTypes", "listAssocs")}
${cd4c.method("public void buildJson(${className}ValidationResult obj, JsonPrinter jsonPrinter)")}
List<ValidationMessage> validationMessages = new ArrayList<>();
<#list attributes as attr>
  validationMessages = obj.get${attr?cap_first}ValidationMessages();
  jsonPrinter.beginArray("messages");
  for(ValidationMessage msg : validationMessages) {
    jsonPrinter.beginObject();
    jsonPrinter.member("message", msg.getMessage());
    jsonPrinter.endArray();
    jsonPrinter.member("status", msg.getStatus().ordinal());
    jsonPrinter.endObject();
  }
  jsonPrinter.endArray();
</#list>
<#list assocTypes as assocType>
  if(obj.has${assocs[assocType_index]?cap_first}ValidationResult()) {
    ${assocType}ValidationResultDeSer ${assocs[assocType_index]}ValidationResultdeSer = new ${assocType}ValidationResultDeSer();
    ${assocs[assocType_index]}ValidationResultdeSer.buildJson(obj.get${assocs[assocType_index]?cap_first}ValidationResult(), jsonPrinter);
  }
</#list>
<#list listAssocTypes as listAssocType>
  jsonPrinter.beginArray("${listAssocType}ValidationResults");
  for(${listAssocType}ValidationResult object : obj.get${listAssocs[listAssocType_index]?cap_first}ValidationResults()) {
    ${listAssocType}ValidationResultDeSer ${listAssocs[listAssocType_index]}ValidationResultdeSer = new ${listAssocType}ValidationResultDeSer();
    ${listAssocs[listAssocType_index]}ValidationResultdeSer.buildJson(object, jsonPrinter);
  }
  jsonPrinter.endArray();
</#list>
