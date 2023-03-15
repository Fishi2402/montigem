<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
<#--${cd4c.method("public void ${className?uncap_first}Get (@ShellOption(value = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}Get (long id)")}
  sendCommandToServerAndReceiveResult(new ${className?cap_first}_get(id));