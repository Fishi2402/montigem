<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes")}
${cd4c.method("public void buildJson(${className} obj, JsonPrinter jsonPrinter)")}
<#list attributes as attr>
  serialize${attr.getName()?cap_first}(obj, jsonPrinter);
</#list>
  serialize_Addons(obj, jsonPrinter);
