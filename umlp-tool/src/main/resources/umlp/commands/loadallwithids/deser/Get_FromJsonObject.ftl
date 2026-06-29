<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cmdName")}
${cd4c.method("protected Optional<${cmdName}> fromJsonObject(JsonObject jsonObject)")}
  List<Long> gemIDs = new ArrayList<>();
  if (!jsonObject.getMember("gemIDs").isJsonObject() && !jsonObject.getMember("gemIDs").isJsonArray()) {
    return Optional.of(new ${cmdName}(new ArrayList<>()));
  }
  if (jsonObject.getMember("gemIDs").isJsonArray()) {
    for (JsonElement jsonElement : jsonObject.getMember("gemIDs").getAsJsonArray().getValues()) {
      Long l = Long.valueOf(jsonElement.getAsJsonString().getValue());
      gemIDs.add(l);
    }
  } else {
    for (String gemId : jsonObject.getMember("gemIDs").getAsJsonObject().getMember("array")
        .getAsJsonArray().get(0).getAsJsonString().getValue().split(",")) {
      gemIDs.add(Long.valueOf(gemId));
    }
  }
  return Optional.of(new ${cmdName}(gemIDs));
