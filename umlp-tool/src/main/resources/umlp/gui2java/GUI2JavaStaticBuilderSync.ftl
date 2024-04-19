<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "keys")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("public void init${name?cap_first}(${argsString})")}
${tc.includeArgs("umlp.gui2java.GUI2JavaBuilder", [type, tmpVar, manager])}
<#list keys as key>
  <#assign i = key?index - 1>
  this.${name}
  <#if i > -1>
    <#list keys[0..i] as prev>
      .get(${prev})
    </#list>
  </#if>
  <#if key?has_next>
      .put(${key}, new HashMap<>());
  </#else>
      .put(${key}, tmpVar);
  </#if>
</#list>
;