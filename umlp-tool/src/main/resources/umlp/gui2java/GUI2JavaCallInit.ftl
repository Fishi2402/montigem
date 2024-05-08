<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initList")}

<#list initList as init>
this.${init.getName()}();
</#list>