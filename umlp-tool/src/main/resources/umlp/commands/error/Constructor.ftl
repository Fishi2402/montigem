<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "attributename")}
${cd4c.constructor("public ${classname?cap_first}_error_${attributename?uncap_first}(long gemId)")}

super("${classname?cap_first}_error_${attributename?uncap_first}");
this.gemId = gemId;