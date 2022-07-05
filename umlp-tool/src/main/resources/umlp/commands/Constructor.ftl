<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "classend")}
${cd4c.constructor("public ${classname}${classend}(${classname} ${classname?uncap_first})")}

super("${packageName}.commands.${classname}_create");

this.${classname?uncap_first} = ${classname?uncap_first};