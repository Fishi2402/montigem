<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "domainObjGetter", "attrName", "params")}
<#assign paramString = genHelper.params2String(params)>
${cd4c.method("public GemTextInputValidity get${methodName?cap_first}(${paramString})")}

Optional<ValidationError> errorOpt = ${domainObjGetter}.get${attrName?cap_first}ValidationError();
if(errorOpt.isPresent()){
  ValidationError error = errorOpt.get();
  return new GemTextInputValidity("Error", error.getShortError());
}

return null;
