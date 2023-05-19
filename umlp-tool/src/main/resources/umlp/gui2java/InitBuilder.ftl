<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("builders", "manager")}
<#list builders as builder>
<#assign firstLetter = builder?lower_case>
<#assign uncapBuilder = firstLetter + builder[1..]>
// this.${builder} = ${manager}.${uncapBuilder}();
</#list>