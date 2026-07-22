<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superType", "extendingTypes" , "typeToManagerMap")}
${cd4c.method("protected Optional<${superType}> _do${superType}Opt(long gemId)")}

<#-- Cache-first: check extending managers before a super fetch (monticore#5062). -->
<#list extendingTypes as extendingType>
if (${typeToManagerMap[extendingType]}.isLoaded${extendingType}(gemId)) {
  return ${typeToManagerMap[extendingType]}.${extendingType?uncap_first}Opt(gemId).map(x -> (${superType}) x);
}
</#list>
return super._do${superType}Opt(gemId);
