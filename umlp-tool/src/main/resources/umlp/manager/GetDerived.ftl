<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static ${name} get${name}()")}

  return getSingleton()._doGet${name}();
<#else>
  ${cd4c.method("protected abstract ${name} _doGet${name}()")}
</#if>
