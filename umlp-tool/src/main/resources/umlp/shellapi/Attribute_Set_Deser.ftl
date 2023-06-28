<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType", "isAssoc")}
${cd4c.method("public void ${className?uncap_first}Set${fieldName?cap_first} (long id, String value)")}
Optional<${className}> ${className?uncap_first} = ${cdName}Manager.${className?uncap_first}Opt(id);
if(${className?uncap_first}.isPresent()){
  ${className?uncap_first}.get().set${fieldName?cap_first}(new umlp.backendrte.common.${fieldType?split(".")?last}DeSer().deserialize(value).get());
}
