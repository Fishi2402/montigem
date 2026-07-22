<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packagename", "managerName")}
${cd4c.method("public SimpleResult<${classname}> doAction()")}

Optional<${classname}> ${classname?uncap_first} = ${managerName}.${classname?uncap_first}Opt(gemId);
if(${classname?uncap_first}.isPresent()){
  // Use the runtime type as typeParameter so the receiver can pick the concrete
  // DeSer even when the declared type is abstract or an interface (monticore#5062)
  String tp = "${packagename}.${classname}";
  if (${classname?uncap_first}.get().getDeSer() != null
      && ${classname?uncap_first}.get().getDeSer().getTypeIdentifier() != null) {
    tp = ${classname?uncap_first}.get().getDeSer().getTypeIdentifier();
  }
  return new SimpleResult<>(getId(), ${classname?uncap_first}.get(), tp);
} else {
  return new SimpleResult<>(getId(), LoadingState.EMPTY, "${packagename}.${classname}");
}
