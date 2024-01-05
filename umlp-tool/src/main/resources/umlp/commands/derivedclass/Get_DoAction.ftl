<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "attribute", "returntype" ,"managerName", "returnedtype", "isList")}
${cd4c.method("public CommandResult<" + returntype + "> doAction()")}

<#if !isList>
  return new SimpleResult<>(getId(), ${managerName}.get${clazz}().get${attribute?cap_first}(), "${returnedtype}");
<#else>
  return ListResult.of(getId(), ${managerName}.get${clazz}().get${attribute?cap_first}(), "${returnedtype}");
</#if>
