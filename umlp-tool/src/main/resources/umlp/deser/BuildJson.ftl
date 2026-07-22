<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes", "assocs")}
${cd4c.method("public void buildJson(${className} obj, JsonPrinter jsonPrinter)")}
  jsonPrinter.member("gemId", obj.getGemId());
  <#-- Runtime type discriminator: lets receivers pick the concrete DeSer (monticore#5062) -->
  jsonPrinter.member("@type", getTypeIdentifier());
<#list attributes as attr>
  serialize${attr?cap_first}(obj, jsonPrinter);
</#list>
<#list assocs as assoc>
  serialize${assoc?cap_first}(obj, jsonPrinter);
</#list>
  serialize_Addons(obj, jsonPrinter);
