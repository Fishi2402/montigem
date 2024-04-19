<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "tmp", "listItems")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
${type} ${tmp} = new ArrayList<>();
<#list listItems as item>
${tmp}.add(${item});
</#list>