<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "attributename", "typename", "packagename")}
<#assign filter = "filter">
${cd4c.constructor("public ${classname?cap_first}${typename}${attributename?uncap_first}(String ${filter})")}
super("<#if packagename != "">${packagename}.</#if>${classname?cap_first}${typename}${attributename?uncap_first}", ${filter});