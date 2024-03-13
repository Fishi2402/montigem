<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
${cd4c.method("public String serialize(" + className + "ValidationResult obj)")}
let printer: any = {};
this.buildJson(obj, printer);
return JSON.stringify(printer);