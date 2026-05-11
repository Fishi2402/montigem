<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "invariants", "domainClassName")}
${cd4c.method("public Optional<ValidationError> get${attrName?cap_first}ValidationError()")}

Optional<ValidationError> res = Optional.empty();
<#list invariants as inv>
  res = ${inv.className}.${inv.errorMethodName}((${domainClassName}) this);
  if (res.isPresent()) {
    return res;
  }
</#list>
return res;
