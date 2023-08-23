<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType")}
${cd4c.method("protected Gem<${fieldType}> deserialize${fieldName?cap_first} (any json)")}
  if (json["${fieldName}"]) {
    return ${cdName}.create${fieldType}Gem(json["${fieldName}"]);
  }
  return new Gem<${fieldType}>().Gem1();
