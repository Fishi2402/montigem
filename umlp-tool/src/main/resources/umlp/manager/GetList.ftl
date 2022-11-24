<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static GemList<${name}> get${name}List()")}

  return getSingleton()._doGet${name}List();
<#else>
  ${cd4c.method("protected abstract GemList<${name}> _doGet${name}List()")}
</#if>