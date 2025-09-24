<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","initializationTypeName", "typeParameter", "isList")}
<#if isList>
  ${cd4c.method("void notifySet${name?cap_first}(${initializationTypeName} ${initializationTypeName?uncap_first}, int idx, ${typeParameter} oldValue, ${typeParameter} o)")}
<#else>
  ${cd4c.method("void notifySet${name?cap_first}(${initializationTypeName} ${initializationTypeName?uncap_first}, ${typeParameter} oldValue, ${typeParameter} o)")}
</#if>
