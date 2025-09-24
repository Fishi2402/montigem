<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName")}
${cd4c.method("public String serialize(" + initializationTypeName + "ValidationResult obj)")}
  JsonPrinter jsonPrinter = new JsonPrinter(true);
  jsonPrinter.beginObject();
  buildJson(obj, jsonPrinter);
  jsonPrinter.endObject();
  return jsonPrinter.toString();
