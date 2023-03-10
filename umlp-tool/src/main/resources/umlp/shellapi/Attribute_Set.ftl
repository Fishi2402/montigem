<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName", "fieldType")}
<#--${cd4c.method("public void ${className?uncap_first}Set${fieldName?cap_first} (@ShellOption(value = \"--id\") long id, @ShellOption(value = \"--value\") ${fieldType} value)")}-->
${cd4c.method("public void ${className?uncap_first}Set${fieldName?cap_first} (long id, ${fieldType} value)")}
  sendCommand(new ${className?cap_first}_set${fieldName?cap_first}(id, value));
