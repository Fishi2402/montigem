<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "cdName", "fieldName")}
${cd4c.method("public void set${fieldName?cap_first} (String ${fieldName})")}
  ${className}.${fieldName} = ${fieldName};
  Log.info("Set ${fieldName} to: " + ${fieldName}, "${cdName} API");