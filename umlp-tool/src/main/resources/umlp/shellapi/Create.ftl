<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "parameters", "domainAttributNames", "deserAttributeNamesAndDeSer", "assocNames")}
<#--${cd4c.method("public void ${className?uncap_first}Get (@ShellOption(__val = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}Create (${parameters})")}
  Optional<${className?cap_first}> ${className?uncap_first} = ${cdName}Manager.${className?uncap_first}Builder()
  <#list domainAttributNames as domainAttribute>
    .${domainAttribute}(${domainAttribute})
  </#list>
  <#list deserAttributeNamesAndDeSer?keys as domainAttribute>
    .${domainAttribute}(new ${deserAttributeNamesAndDeSer[domainAttribute]}().deserialize(${domainAttribute}).get())
  </#list>
  <#list assocNames as assocName>
    .${assocName}Gem(new umlp.backendrte.common.Gem(${assocName}, umlp.backendrte.common.LoadingState.NOT_YET_LOADED))
  </#list>
    .build();

  if (${className?uncap_first}.isPresent()) {
    Log.info(
      "Built ${className?uncap_first}: " + new ${className}DeSer().serialize(${className?uncap_first}.get()),
      UMLP_SHELL_API);
  } else {
    Log.warn("Could not create ${className}");
  }
