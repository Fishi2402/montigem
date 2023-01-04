<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType")}
${cd4c.method("protected Gem<${fieldType}> deserialize${fieldName?cap_first} (JsonObject json)")}
  <#-- TODO: default values -->
  return ${cdName}Manager.create${fieldType}Gem(json.getMember("${fieldName}").getAsJsonNumber().getNumberAsLong());
