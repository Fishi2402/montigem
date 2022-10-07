<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType")}
${cd4c.method("protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}
  <#-- TODO: default values -->
  List<Long> res = new ArrayList<>();
  for(JsonElement e : json.getMember("${fieldName}").getAsJsonArray().getValues()){
    res.add(e.getAsJsonNumber().getNumberAsLong());
  }
  <#-- TODO: createX needs Domain object, which is not yet accesible -->
  return ${cdName}Manager.create${fieldName?cap_first}(json.getMember("gemId").getAsJsonNumber().getNumberAsLong(), json.getMember("listId").getAsJsonNumber().getNumberAsLong(), res);
