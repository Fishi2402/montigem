<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "listItems", "keys", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("public void init${name?cap_first}(${argsString})")}
${cdPrinter.printType(mcType)} ${tmpVar} = new ArrayList<>();
<#list listItems as item>
${tmpVar}.add(${item});
</#list>

if (this.${name} == null) {
  this.${name} = new HashMap<>();
}
<#list keys as key>
  <#assign i = key?index - 1>
  <#if key?has_next>
    if (!this.${name}
      <#if i gt -1>
        <#list keys[0..i] as prev>
          .get(${prev.getName()})
        </#list>
      </#if>
          .containsKey(${key.getName()})) {
  </#if>
  this.${name}
  <#if i gt -1>
    <#list keys[0..i] as prev>
      .get(${prev.getName()})
    </#list>
  </#if>
  <#if key?has_next>
      .put(${key.getName()}, new HashMap<>());
  }
  <#else>
      .put(${key.getName()}, ${tmpVar});
  </#if>
</#list>
;
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}