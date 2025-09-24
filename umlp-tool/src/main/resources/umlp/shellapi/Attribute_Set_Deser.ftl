<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "fieldTypeCdName", "initializationTypeName", "fieldName", "fieldType", "isAssoc")}
${cd4c.method("public void ${initializationTypeName?uncap_first}Set${fieldName?cap_first} (long id, String __val)")}
Optional<${initializationTypeName}> ${initializationTypeName?uncap_first} = ${cdName}Manager.${initializationTypeName?uncap_first}Opt(id);
if(${initializationTypeName?uncap_first}.isPresent()){
  ${initializationTypeName?uncap_first}.get().set${fieldName?cap_first}(new umlp.backendrte.common.${fieldType?split(".")?last}DeSer().deserialize(__val).get());
}
