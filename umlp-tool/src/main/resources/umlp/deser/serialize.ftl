<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes")}
${cd4c.method("public String serialize(" + className + " obj)")}
  JsonPrinter jsonPrinter = new JsonPrinter(true);
  jsonPrinter.beginObject();
  <#list attributes as attr>
  serialize${attr.getName()?cap_first}(obj, jsonPrinter);
  </#list>
  serialize_Addons(obj, jsonPrinter);
  jsonPrinter.endObject();
  return jsonPrinter.toString();
