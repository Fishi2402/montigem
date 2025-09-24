<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "initializationTypeName", "parameters", "domainAttributNames", "deserAttributeNamesAndDeSer", "assocNames")}
<#--${cd4c.method("public void ${initializationTypeName?uncap_first}Get (@ShellOption(__val = \"--id\") long id)")}-->
${cd4c.method("public void ${initializationTypeName?uncap_first}Create (${parameters})")}
  Optional<${initializationTypeName?cap_first}> ${initializationTypeName?uncap_first} = ${cdName}Manager.${initializationTypeName?uncap_first}Builder()
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

  if (${initializationTypeName?uncap_first}.isPresent()) {
    Log.info(
      "Built ${initializationTypeName?uncap_first}: " + new ${initializationTypeName}DeSer().serialize(${initializationTypeName?uncap_first}.get()),
      UMLP_SHELL_API);
  } else {
    Log.warn("Could not create ${initializationTypeName}");
  }
