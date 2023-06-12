<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Promise<long> getFreeGemId()")}

  return this.getSingleton()._doGetFreeGemId();
<#else>
  ${cd4c.method("protected abstract Promise<long> _doGetFreeGemId()")}
</#if>