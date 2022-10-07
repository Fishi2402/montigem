<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType", "innerType")}
${cd4c.method("protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}
  <#-- TODO: default values -->
  return ${cdName}Manager.create${innerType}Gem(json.getMember("fieldName").getAsJsonNumber().getNumberAsLong());
