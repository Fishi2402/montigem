<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "fieldName")}
${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + initializationTypeName + " obj, any printer)")}
  printer["${fieldName}"] = [];
  for(const gemId of obj._internal_get${fieldName?cap_first}GemList().getIDs()){
      printer["${fieldName}"].push(gemId);
  }
