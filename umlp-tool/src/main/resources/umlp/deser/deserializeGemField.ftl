<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("managerName", "className", "fieldName", "fieldType")}
${cd4c.method("protected Gem<${fieldType}> deserialize${fieldName?cap_first} (JsonObject json)")}
  if(!json.hasMember("${fieldName}")){
    return new Gem<>();
  }
  return ${managerName}.create${fieldType}Gem(json.getMember("${fieldName}").getAsJsonNumber().getNumberAsLong());
