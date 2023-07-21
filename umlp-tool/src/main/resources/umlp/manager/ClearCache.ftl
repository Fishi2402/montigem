<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static void TESTING_clearCache()")}
  getSingleton()._TESTING_clearCache();
<#else>
  ${cd4c.method("protected abstract void _TESTING_clearCache()")}
</#if>
