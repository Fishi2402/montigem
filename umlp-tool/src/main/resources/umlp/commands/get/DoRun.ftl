<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packagename")}
${cd4c.method("public SimpleResult<${classname}> doRun()")}

Optional<${classname}> ${classname?uncap_first} = FlensburgManager.${classname?uncap_first}Opt(gemId);
    if(${classname?uncap_first}.isPresent()){
    return new SimpleResult<>(getId(), ${classname?uncap_first}.get(), "${packagename}.${classname}");
    } else {
    return new SimpleResult<>(getId(), LoadingState.EMPTY, "${packagename}.${classname}");
    }