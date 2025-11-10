<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command")}
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}

  if (jsonObject.hasStringMember("filter")) {
   return Optional.of(new ${command}(jsonObject.getStringMember("filter")));
  }
  return Optional.of(new ${command}());