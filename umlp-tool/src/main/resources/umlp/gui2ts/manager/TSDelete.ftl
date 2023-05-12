<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static boolean delete${name}(Long gemId)")}

  return this.getSingleton()._doDelete${name}(gemId);
<#else>
  ${cd4c.method("protected abstract boolean _doDelete${name}(Long gemId)")}
</#if>