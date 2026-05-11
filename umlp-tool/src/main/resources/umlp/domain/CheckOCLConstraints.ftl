<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("invariants", "domainClassName")}
${cd4c.method("public boolean checkOCLConstraints()")}

boolean res = true;
<#list invariants as inv>
  res &= ${inv.className}.${inv.methodName}((${domainClassName}) this);
</#list>
return res;
