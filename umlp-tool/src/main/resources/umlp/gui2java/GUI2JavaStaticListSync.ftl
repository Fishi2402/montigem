<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "listItems", "keys")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("public void sync${name?cap_first}(${argsString})")}
if (${tc.includeArgs("umlp.gui2java.GUI2JavaStaticObjectSyncCheck", [keys])}) {
  return;
}
${tc.includeArgs("umlp.gui2java.GUI2JavaList", [type, tmpVar, listItems])}
<#list keys as key>
  <#assign i = key?index - 1>
  this.${name}
  <#if i gt -1>
    <#list keys[0..i] as prev>
      .get(${prev.getName()})
    </#list>
  </#if>
  <#if key?has_next>
      .put(${key.getName()}, new HashMap<>());
  <#else>
      .put(${key.getName()}, ${tmpVar});
  </#if>
</#list>
;