<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "command")}
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}

Optional<${className}> o = new ${className}DeSer().deserialize(jsonObject.getObjectMember("${className?uncap_first}"));
if(o.isEmpty()){
  return Optional.empty();
}
return Optional.of(new ${command}(o.get()));
