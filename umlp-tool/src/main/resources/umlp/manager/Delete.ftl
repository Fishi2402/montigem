<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static void delete${name}(${name} o)")}

  getSingleton()._doDelete${name}(o);
<#else>
  ${cd4c.method("protected abstract void _doDelete${name}(${name} o)")}
</#if>