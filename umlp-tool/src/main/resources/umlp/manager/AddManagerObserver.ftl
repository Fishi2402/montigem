<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#assign type="${name}Observer" />
<#if delegateFlag>
  ${cd4c.method("public static void addObserver(${type} obs)")}
  getSingleton()._addObserver(obs);
<#else>
  ${cd4c.method("public void _addObserver(${type} obs)")}
  ${name?uncap_first}Observers.add(obs);
</#if>
