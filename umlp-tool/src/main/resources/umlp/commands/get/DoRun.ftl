<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packagename", "managerName")}
${cd4c.method("public SimpleResult<${classname}> doRun()")}

if(checkAccessPolicy()){
  Optional<${classname}> ${classname?uncap_first} = ${managerName}.${classname?uncap_first}Opt(gemId);
    if(${classname?uncap_first}.isPresent()){
      return new SimpleResult<>(getId(), ${classname?uncap_first}.get(), "${packagename}.${classname}");
    } else {
      return new SimpleResult<>(getId(), LoadingState.EMPTY, "${packagename}.${classname}");
    }
}
else {
  return new SimpleResult<>(getId(), LoadingState.NOT_AUTHORIZED, "${packagename}.${classname}");
}
