<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superType", "extendingTypes" , "typeToManagerMap")}
${cd4c.method("protected Optional<${superType}> _do${superType}Opt(long gemId)")}

  if(super._do${superType}Opt(gemId).isPresent()) {
    return super._do${superType}Opt(gemId);
  }
<#list extendingTypes as extendingType>
  else if(${typeToManagerMap[extendingType]}.${extendingType?uncap_first}Opt(gemId).isPresent()) {
    return ${typeToManagerMap[extendingType]}.${extendingType?uncap_first}Opt(gemId).map(x -> (${superType}) x);
  }
</#list>
  else {
    Log.warn("0x12002: Could not load ${superType} with gemId "+gemId+" as ${superType} or any of its subtypes");
    return Optional.empty();
  }

