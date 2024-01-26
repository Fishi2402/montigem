<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "simpleAttributes", "gemAttributes", "gemListAttributes")}
${cd4c.method("public Optional<${className}ValidationResult> deserialize(JsonObject json)")}
return Optional.of(new ${className}ValidationResult());
