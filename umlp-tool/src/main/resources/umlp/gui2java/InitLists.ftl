<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("uniqueVarName", "setterName", "isItemPrimitive", "values")}
    List<TableColumn> ${uniqueVarName} = new ArrayList();
  <#if isItemPrimitive>
  <#list values as v>
      ${uniqueVarName}.add(${v});
  </#list>
  <#else>
  <#list values as v>
      ${uniqueVarName}.add(new ${v});
  </#list>
  </#if>
    ${setterName}(${uniqueVarName});