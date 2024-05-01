<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "manager", "keys", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("public void init${name?cap_first}(${argsString})")}
${manager}.${mcType.printType()?uncap_first}Builder().then((${tmpVar}: ${cdPrinter.printType(mcType)}) => {
  <#list keys as key>
    <#assign i = key?index - 1>
    this.${name}
    <#if i gt -1>
      <#list keys[0..i] as prev>
        .get(${prev.getName()})
      </#list>
    </#if>
    <#if key?has_next>
        .set(${key.getName()}, new Map());
    <#else>
        .set(${key.getName()}, ${tmpVar});
    </#if>
  </#list>
  ;
});