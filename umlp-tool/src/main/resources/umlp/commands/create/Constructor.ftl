<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName")}
${cd4c.constructor("public ${classname}()")}

super("${packageName}.commands.${classname}_create");

this.${classname?uncap_first} = ${classname?uncap_first};