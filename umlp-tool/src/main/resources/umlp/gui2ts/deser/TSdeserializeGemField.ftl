<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType")}
${cd4c.method("protected Gem<${fieldType}> deserialize${fieldName?cap_first} (any json)")}
  let res = new Gem<${fieldType}>()
  if (json["${fieldName}"]) {
    ${cdName}.create${fieldType}Gem(json["${fieldName}"]).then(v => res.Gem2(v.get()));
  }
  return res;
