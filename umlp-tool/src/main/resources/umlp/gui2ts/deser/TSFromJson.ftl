<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName")}
${cd4c.method("public Optional<${initializationTypeName}> fromJson(any jsonElement)")}

return this.deserialize(jsonElement);
