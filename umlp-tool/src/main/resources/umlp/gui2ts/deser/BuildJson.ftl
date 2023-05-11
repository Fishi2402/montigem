<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes", "assocs")}
${cd4c.method("public void buildJson(${className} obj, JsonPrinter jsonPrinter)")}
  jsonPrinter.member("gemId", obj.getGemId());
<#list attributes as attr>
  this.serialize${attr?cap_first}(obj, jsonPrinter);
</#list>
<#list assocs as assoc>
  this.serialize${assoc?cap_first}(obj, jsonPrinter);
</#list>
  this.serialize_Addons(obj, jsonPrinter);
