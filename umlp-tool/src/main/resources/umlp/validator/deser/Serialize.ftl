<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
${cd4c.method("public String serialize(" + className + "ValidationResult obj)")}
  JsonPrinter jsonPrinter = new JsonPrinter(true);
  jsonPrinter.beginObject();
  buildJson(obj, jsonPrinter);
  jsonPrinter.endObject();
  return jsonPrinter.toString();
