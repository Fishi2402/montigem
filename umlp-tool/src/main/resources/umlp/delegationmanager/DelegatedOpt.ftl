<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superType", "extendingTypes" ,"extendingManagerName", "delegatedManager")}
${cd4c.method("protected Optional<${superType}> _do${superType}Opt(long gemId)")}

  if(super._do${superType}Opt(gemId).isPresent()) {
    return super._do${superType}Opt(gemId);
  }
<#list extendingTypes as extendingType>
  else if(${extendingManagerName}.${extendingType?uncap_first}Opt(gemId).isPresent()) {
    return ${extendingManagerName}.${extendingType?uncap_first}Opt(gemId).map(x -> (${superType}) x);
  }
</#list>
  else {
    return Optional.empty();
  }

