<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName")}
${cd4c.method("protected List<Long> deserialize${fieldName?cap_first} (any json)")}
  <#-- TODO: default values -->
  let res: number[] = [];
  if(!json["${fieldName}"]){
    return res;
  }
  for(const e of json["${fieldName}"]){
    res.push(e);
  }
  return res;