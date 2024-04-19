<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "listItems")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
${type} ${name} = new ArrayList<>();
<#list listItems as item>
${name}.add(${item});
</#list>