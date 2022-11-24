<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static long getFreeGemId()")}

  return getSingleton()._doGetFreeGemId();
<#else>
  ${cd4c.method("protected abstract long _doGetFreeGemId()")}
</#if>