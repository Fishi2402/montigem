<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "tmp", "manager")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
${type} ${tmp} = ${manager}.${type?uncap_first}Builder();
