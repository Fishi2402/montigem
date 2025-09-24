<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "attributes", "assocs")}
${cd4c.method("public void buildJson(${initializationTypeName} obj, JsonPrinter jsonPrinter)")}
  jsonPrinter.member("gemId", obj.getGemId());
<#list attributes as attr>
  serialize${attr?cap_first}(obj, jsonPrinter);
</#list>
<#list assocs as assoc>
  serialize${assoc?cap_first}(obj, jsonPrinter);
</#list>
  serialize_Addons(obj, jsonPrinter);
