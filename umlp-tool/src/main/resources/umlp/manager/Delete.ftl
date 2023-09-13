<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static boolean delete${name}(Long gemId)")}

  return getSingleton()._doDelete${name}(gemId);
<#else>
  ${cd4c.method("protected boolean _doDelete${name}(Long gemId)")}
  return _doDelete${name}(gemId, NotificationScope.ALL);
</#if>
