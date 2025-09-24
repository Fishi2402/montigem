<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "fieldName")}
${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + initializationTypeName + " obj, JsonPrinter printer)")}
  printer.member("${fieldName}", obj.get${fieldName?cap_first}Gem().getGemId());
