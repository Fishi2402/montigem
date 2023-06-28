<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Promise<GemList<${name}>> get${name}List()")}

  return this.getSingleton()._doGet${name}List();
<#else>
  ${cd4c.method("protected abstract Promise<GemList<${name}>> _doGet${name}List()")}
</#if>