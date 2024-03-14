<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "clazz", "reportError")}
${cd4c.method("protected boolean check${name?cap_first}Present()")}

<#if reportError>
if(this._${name} == null){
  ${clazz}ErrorCollector.report${name?cap_first}Invalid(this);
}
return this._${name} != null;
<#else>
return true;
</#if>
