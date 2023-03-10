<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName")}
<#--${cd4c.method("public void ${className?uncap_first}Get${fieldName?cap_first} (@ShellOption(value = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}Get${fieldName?cap_first} (long id)")}
sendCommand(new ${className?cap_first}_get${fieldName?cap_first}(id, value));
