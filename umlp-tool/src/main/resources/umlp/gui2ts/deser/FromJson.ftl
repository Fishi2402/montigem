<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
${cd4c.method("public Optional<${className}> fromJson(JsonElement jsonElement)")}

return this.deserialize(jsonElement.getAsJsonObject());
