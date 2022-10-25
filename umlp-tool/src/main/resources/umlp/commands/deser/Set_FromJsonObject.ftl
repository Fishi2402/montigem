<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "json")}
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}

// TODO
// return Optional.of(new ${command}(jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong(), jsonObject.getMember("${attrName}").${json}));
return Optional.empty();