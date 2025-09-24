<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName")}
${cd4c.method("public Optional<${initializationTypeName}> fromJson(JsonElement jsonElement)")}

return deserialize(jsonElement.getAsJsonObject());
