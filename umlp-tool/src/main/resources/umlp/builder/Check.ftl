<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "clazz", "reportError")}
${cd4c.method("protected boolean check${name?cap_first}Present()")}

<#if reportError>
if(!${name}Present){
  ${clazz}ErrorCollector.report${name?cap_first}Invalid(this);
}
return ${name}Present;
<#else>
return true;
</#if>
