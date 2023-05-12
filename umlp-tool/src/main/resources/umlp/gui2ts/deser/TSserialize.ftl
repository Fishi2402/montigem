<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
${cd4c.method("public String serialize(" + className + " obj)")}
  let jsonPrinter: JsonPrinter = new JsonPrinter(true);
  jsonPrinter.beginObject();
  this.buildJson(obj, jsonPrinter);
  jsonPrinter.endObject();
  return jsonPrinter.toString();
