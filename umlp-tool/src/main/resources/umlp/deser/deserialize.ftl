<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "attributes")}
${cd4c.method("public ${className} deserialize(String jsonString)")}
  <#-- TODO: shared JsonParser instance? -->
  JsonElement elem = new JsonParser().parse(jsonString);
  JsonObject json = elem.getAsJsonObject();
  <#-- TODO: normal Builder can not be used here, since it will create objects with new gemIds! -->
  <#-- TODO AHe: continue here -->
  return null;