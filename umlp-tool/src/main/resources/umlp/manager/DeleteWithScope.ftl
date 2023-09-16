<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static boolean delete${name}(Long gemId, NotificationScope scope)")}

  return getSingleton()._doDelete${name}(gemId, scope);
<#else>
  ${cd4c.method("protected abstract boolean _doDelete${name}(Long gemId, NotificationScope scope)")}
</#if>
