<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "parameters", "domainAttributNames")}
<#--${cd4c.method("public void ${className?uncap_first}Get (@ShellOption(value = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}Create (${parameters})")}
  ${className?cap_first} ${className?uncap_first} = new ${className?cap_first}Builder(getFreeGemId())
  <#list domainAttributNames as domainAttribute>
    .${domainAttribute}(${domainAttribute})
  </#list>
  ;
  sendCommandToServerAndReceiveResult(new ${className?cap_first}_create(${className?uncap_first}));