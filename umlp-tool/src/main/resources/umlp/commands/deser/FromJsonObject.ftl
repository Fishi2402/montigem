<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "command")}
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}

Optional<${initializationTypeName}> o = new ${initializationTypeName}DeSer().deserialize(jsonObject.getObjectMember("${initializationTypeName?uncap_first}"));
if(o.isEmpty()){
  return Optional.empty();
}
return Optional.of(new ${command}(o.get()));
