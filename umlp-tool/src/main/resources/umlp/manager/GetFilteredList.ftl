<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static GemList<${name}> getFiltered${name}List(String filter)")}

  return getSingleton()._doGetFiltered${name}List(filter);
<#else>
  ${cd4c.method("protected abstract GemList<${name}> _doGetFiltered${name}List(String filter)")}
</#if>