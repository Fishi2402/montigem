<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "classend")}
${cd4c.constructor("public ${classname}${classend}(${classname} ${classname?uncap_first})")}

super("${packageName}.${classname}${classend}");

this.${classname?uncap_first} = ${classname?uncap_first};