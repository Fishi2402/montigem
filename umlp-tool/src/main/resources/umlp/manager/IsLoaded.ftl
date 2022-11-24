<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static boolean isLoaded${name}(long gemId)")}

  return getSingleton()._isLoaded${name}(gemId);
<#else>
  ${cd4c.method("protected abstract boolean _isLoaded${name}(long gemId)")}
</#if>