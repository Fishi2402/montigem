<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "domainObjGetter", "attrName", "targetType", "params", "additionalValidationSources")}
<#assign paramString = genHelper.params2String(params)>
${cd4c.method("public ${targetType} get${methodName?cap_first}(${paramString})")}

String errorMessage = null;
Optional<ValidationError> errorOpt = ${domainObjGetter}.get${attrName?cap_first}ValidationError();
if(errorOpt.isPresent()){
  ValidationError error = errorOpt.get();
  errorMessage = error.getShortError();
}
<#list additionalValidationSources as source>
errorOpt = ${source.domainObjGetter}.get${source.attrName?cap_first}ValidationError();
if(errorOpt.isPresent()){
  ValidationError error = errorOpt.get();
  errorMessage = errorMessage == null ? error.getShortError() : errorMessage + "\n" + error.getShortError();
}
</#list>

return errorMessage == null ? null : new ${targetType}("Error", errorMessage);
