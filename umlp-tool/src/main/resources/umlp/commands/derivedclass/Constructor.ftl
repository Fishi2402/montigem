<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packagename", "domainclassname", "attributename" )}
${cd4c.constructor("public ${classname?cap_first}()")}

super("<#if packagename != "">${packagename}.</#if>${domainclassname?cap_first}_${attributename?cap_first}_get");
