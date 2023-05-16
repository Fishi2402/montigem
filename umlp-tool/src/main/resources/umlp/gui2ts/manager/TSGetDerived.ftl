<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static ${name} get${name}()")}

  return this.getSingleton()._doGet${name}();
<#else>
  ${cd4c.method("protected ${name} _doGet${name}()")}

  return new ${name}();
</#if>