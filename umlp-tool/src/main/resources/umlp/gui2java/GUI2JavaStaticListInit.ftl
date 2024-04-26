<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "listItems", "keys")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("public void init${name?cap_first}(${argsString})")}
if (${tc.includeArgs("umlp.gui2java.GUI2JavaStaticObjectSyncCheck", [keys])}) {
  return;
}
${cdPrinter.printType(mcType)} ${tmpVar} = new ArrayList<>();
<#list listItems as item>
${tmpVar}.add(${item});
</#list>

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