<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "clazz", "reportError", "isList")}
${cd4c.method("protected boolean check${name?cap_first}Present()")}

boolean invalid = this._${name} == null ||
                <#if isList>
                  this._${name}.isEmpty()
                <#else>
                  this._${name}.getGemId() == -1
                </#if>;
<#if reportError>
if(invalid){
  ${clazz}ErrorCollector.report${name?cap_first}Invalid(this);
}

return !invalid;
<#else>
return true;
</#if>
