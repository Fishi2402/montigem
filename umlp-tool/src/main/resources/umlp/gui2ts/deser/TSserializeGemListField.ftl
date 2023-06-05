<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName")}
${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + className + " obj, any printer)")}
  printer["${fieldName}"] = [];
  for(const gemId of obj._internal_get${fieldName?cap_first}GemList().getIDs()){
      printer["${fieldName}"].push(gemId);
  }