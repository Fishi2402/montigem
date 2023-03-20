<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "parameters", "domainAttributNames", "assocNames")}
<#--${cd4c.method("public void ${className?uncap_first}Get (@ShellOption(value = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}Create (${parameters})")}
  ${className?cap_first} ${className?uncap_first} = new ${className?cap_first}Builder(getFreeGemId())
  <#list domainAttributNames as domainAttribute>
    .${domainAttribute}(${domainAttribute})
  </#list>
  <#list assocNames as assocName>
    .${assocName}Gem(new umlp.backendrte.common.Gem(${assocName}, umlp.backendrte.common.LoadingState.NOT_YET_LOADED))
  </#list>
  ;
  sendCommandToServerAndReceiveResult(new ${className?cap_first}_create(${className?uncap_first}));