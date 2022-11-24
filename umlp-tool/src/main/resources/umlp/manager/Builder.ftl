<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "hasDelegate")}
<#if hasDelegate>
  ${cd4c.method("public static ${name}Builder ${name?uncap_first}Builder()")}
  return getSingleton()._do${name}Builder();
<#else>
  ${cd4c.method("protected ${name}Builder _do${name}Builder()")}
  return getSingleton()._do${name}Builder(_doGetFreeGemId());
</#if>