<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName")}
${cd4c.method("public String serialize(" + initializationTypeName + "ValidationResult obj)")}
let printer: any = {};
this.buildJson(obj, printer);
return JSON.stringify(printer);
