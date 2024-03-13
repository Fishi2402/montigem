<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
${cd4c.method("public Optional<${className}ValidationResult> fromJson(JsonElement jsonElement)")}

return deserialize(jsonElement.getAsJsonObject());
