<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
${type} ${name} =  ${manager}.${type?uncap_first}Builder();
