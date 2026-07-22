<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cmdName")}
${cd4c.method("protected Optional<${cmdName}> fromJsonObject(JsonObject jsonObject)")}
  List<Long> gemIDs = new ArrayList<>();
  if (jsonObject.hasMember("gemIDs") && jsonObject.getMember("gemIDs").isJsonArray()) {
    for (JsonElement e : jsonObject.getMember("gemIDs").getAsJsonArray().getValues()) {
      if (e.isJsonString()) {
        gemIDs.add(Long.valueOf(e.getAsJsonString().getValue()));
      } else {
        gemIDs.add(e.getAsJsonNumber().getNumberAsLong());
      }
    }
  }
  return Optional.of(new ${cmdName}(gemIDs));
