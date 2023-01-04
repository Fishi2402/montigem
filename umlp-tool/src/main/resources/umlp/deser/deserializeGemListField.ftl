<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName")}
${cd4c.method("protected List<Long> deserialize${fieldName?cap_first} (JsonObject json)")}
  <#-- TODO: default values -->
  List<Long> res = new ArrayList<>();
  for(JsonElement e : json.getMember("${fieldName}").getAsJsonArray().getValues()){
    res.add(e.getAsJsonNumber().getNumberAsLong());
  }
  return res;