<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName")}
${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + className + " obj, JsonPrinter printer)")}
  printer.beginArray("${fieldName}");
  for(long gemId : obj.get${fieldName?cap_first}GemList().getIDs()){
      printer.value(gemId);
  }
  printer.endArray();