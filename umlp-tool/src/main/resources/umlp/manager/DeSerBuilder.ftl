<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static ${name}DeSerBuilder ${name?uncap_first}DeSerBuilder()")}

  return getSingleton()._do${name}DeSerBuilder();
<#else>
  ${cd4c.method("protected abstract ${name}DeSerBuilder _do${name}DeSerBuilder()")}
</#if>