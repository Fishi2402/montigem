<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("uniqueVarName", "setterName", "isItemPrimitive", "values", "iteratorArgs")}
<#-- TODO AGe: Type -->
const ${uniqueVarName}: Array<any> = [];
<#if isItemPrimitive>
  <#list values as v>
    ${uniqueVarName}.push(${v});
  </#list>
<#else>
  <#list values as v>
    ${uniqueVarName}.push(new ${v});
  </#list>
</#if>
this.${setterName}(${uniqueVarName}<#list iteratorArgs as arg>, null</#list>);