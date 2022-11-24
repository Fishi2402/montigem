<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static void add${name}(${name} ${name?uncap_first})")}

  getSingleton()._doAdd${name}(${name?uncap_first});
<#else>
  ${cd4c.method("protected abstract void _doAdd${name}(${name} o)")}
</#if>