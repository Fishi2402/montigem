<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "fieldTypeCdName", "initializationTypeName", "fieldName", "fieldType", "isAssoc")}
${cd4c.method("public void ${initializationTypeName?uncap_first}Remove${fieldName?cap_first} (long id, String __val)")}
Optional<${initializationTypeName}> ${initializationTypeName?uncap_first} = ${cdName}Manager.${initializationTypeName?uncap_first}Opt(id);
if(${initializationTypeName?uncap_first}.isPresent()){
  ${initializationTypeName?uncap_first}.get().remove${fieldName?cap_first}(new ${fieldType?split(".")?last}DeSer().deserialize(__val).get());
}
