<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "classend")}
${cd4c.constructor("public constructor(${classname} ${classname?uncap_first})")}
public constructor(${classname?uncap_first}: ${classname}) {
  super("${packageName}.${classname}${classend}");
  this.${classname?uncap_first} = ${classname?uncap_first};
}