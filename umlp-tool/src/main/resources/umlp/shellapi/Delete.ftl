<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
<#--${cd4c.method("public void ${className?uncap_first}Delete (@ShellOption(value = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}Delete (long id)")}
  sendCommandToServerAndReceiveResult(new ${className?cap_first}_delete(id));