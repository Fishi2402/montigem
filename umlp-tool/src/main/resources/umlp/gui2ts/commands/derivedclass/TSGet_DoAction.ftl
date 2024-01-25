<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "attribute", "returntype" ,"managerName", "returnedtype", "isList")}
${cd4c.method("public Promise<CommandResult<" + returntype + ">> doRun()")}

<#if !isList>
return ${managerName}.get${clazz}Sure(-1)
.then(der => {
  return new SimpleResult<${returntype}>().SimpleResult1(this.getId(), der.get${attribute?cap_first}(), "${returnedtype}");
});
<#else>
return ${managerName}.get${clazz}Sure(-1)
.then(der => {
  return ListResult.of(this.getId(), der.get${attribute?cap_first}(), "${returnedtype}");
});
</#if>

//return new SimpleResult<>(getId(), ${managerName}.get${clazz}().get${attribute?cap_first}(), "${packagename}.${clazz}_${attribute?cap_first}");
