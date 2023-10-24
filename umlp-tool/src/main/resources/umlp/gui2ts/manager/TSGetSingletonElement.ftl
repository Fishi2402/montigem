<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
    ${cd4c.method("public static Promise<${name}> get${name}()")}

  return this.getSingleton()._doGet${name}();
<#else>
    ${cd4c.method("protected abstract Promise<${name}> _doGet${name}()")}
</#if>
