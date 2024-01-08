<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "attribute", "returntype" ,"managerName", "returnedtype", "isList")}
${cd4c.method("public CommandResult<" + returntype + "> doAction()")}


<#if !isList>
  ${returntype} obj = ${managerName}.get${clazz}().get${attribute?cap_first}();
  if (obj == null) {
    return new SimpleResult<>(getId(), umlp.backendrte.common.LoadingState.EMPTY, "${returnedtype}");
  } else {
    return new SimpleResult<>(getId(), obj, "${returnedtype}");
  }
<#else>
  return ListResult.of(getId(), ${managerName}.get${clazz}().get${attribute?cap_first}(), "${returnedtype}");
</#if>
