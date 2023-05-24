<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName")}
${cd4c.method("protected List<Long> deserialize${fieldName?cap_first} (JsonObject json)")}
  <#-- TODO: default values -->
  let res: number[] = [];
  if(!json.hasMember("${fieldName}")){
    return res;
  }
  for(const e of json.getMember("${fieldName}").getAsJsonArray().getValues()){
    res.add(e.getAsJsonNumber().getNumberAsLong());
  }
  return res;