<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName")}
${cd4c.method("protected Long deserialize${fieldName?cap_first} (JsonObject json)")}
  <#-- TODO: default values -->
  return json.getMember("${fieldName}").getAsJsonNumber().getNumberAsLong();
