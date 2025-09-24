<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName")}
${cd4c.method("public ${initializationTypeName}ValidationResult fromJson(any json)")}
return this.deserialize(json);
