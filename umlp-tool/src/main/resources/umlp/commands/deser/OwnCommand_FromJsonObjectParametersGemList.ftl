<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName")}
${cd4c.method("private List<Long> get${attrName?cap_first}(JsonObject jsonObject)")}
  List<Long> list = new ArrayList<>();
  for (JsonElement e : jsonObject.getMember("${attrName}").getAsJsonArray().getValues()) {
    Long value = e.getAsJsonNumber().getNumberAsLong();
    list.add(value);
  }
  return list;
