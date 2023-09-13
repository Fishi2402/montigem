<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packagename", "managerName")}
${cd4c.method("public SimpleResult<${classname}> doRun()")}

${packagename}.${classname}AccessPolicy accessPolicy = new ${packagename}.${classname}AccessPolicy();
if(accessPolicy.checkPermission(this)){
  Optional<${classname}> ${classname?uncap_first} = ${managerName}.${classname?uncap_first}Opt(gemId);
    if(${classname?uncap_first}.isPresent()){
      return new SimpleResult<>(getId(), ${classname?uncap_first}.get(), "${packagename}.${classname}");
    } else {
      return new SimpleResult<>(getId(), LoadingState.EMPTY, "${packagename}.${classname}");
    }
} else {
  return new ServerError(new SecurityException("Access Policy forbids Command. Do you have the required permissions?"));
}
