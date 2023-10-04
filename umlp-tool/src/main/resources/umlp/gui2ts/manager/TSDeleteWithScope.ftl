<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Promise<boolean> delete${name}2(Long gemId, NotificationScope scope)")}

  return this.getSingleton()._doDelete${name}2(gemId, scope);
<#else>
  ${cd4c.method("protected abstract Promise<boolean> _doDelete${name}2(Long gemId, NotificationScope scope)")}
</#if>
