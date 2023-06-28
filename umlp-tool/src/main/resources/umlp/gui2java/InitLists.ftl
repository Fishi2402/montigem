<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("uniqueVarName", "setterName", "isItemPrimitive", "values", "iteratorArgs")}
<#-- TODO AGe: Type -->
    List ${uniqueVarName} = new ArrayList();
  <#if isItemPrimitive>
  <#list values as v>
      ${uniqueVarName}.add(${v});
  </#list>
  <#else>
  <#list values as v>
      ${uniqueVarName}.add(new ${v});
  </#list>
  </#if>
    // TODO AGe:
    ${setterName}(${uniqueVarName}<#list iteratorArgs as arg>, null</#list>);