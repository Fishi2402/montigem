<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("managerName", "assocName", "assocType")}

${cd4c.method("GemList<${assocType}> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException")}

JsonObject obj = json.getAsJsonObject();
long gemListId = obj.get("listId").getAsLong();
List<Long> ${assocType?uncap_first}Ids = new ArrayList<>();
  if(obj.has("values")) {
  for (JsonElement value : obj.get("values").getAsJsonArray()) {
    ${assocType?uncap_first}Ids.add(value.getAsLong());
  }
  }

  return ${managerName}Manager.create${assocName?cap_first}(gemListId, ${assocType?uncap_first}Ids);