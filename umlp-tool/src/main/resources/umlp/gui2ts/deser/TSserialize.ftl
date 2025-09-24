<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName")}
${cd4c.method("public String serialize(" + initializationTypeName + " obj)")}
  let jsonPrinter: any = {};
  this.buildJson(obj, jsonPrinter);
  return JSON.stringify(jsonPrinter);
