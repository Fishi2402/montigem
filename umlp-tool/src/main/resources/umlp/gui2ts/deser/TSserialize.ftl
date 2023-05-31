<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
${cd4c.method("public String serialize(" + className + " obj)")}
  let jsonPrinter: any = {};
  this.buildJson(obj, jsonPrinter);
  return JSON.stringify(jsonPrinter);
