<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "attrList")}
<#assign pList>
  <#list attrList as attr>${attr.printType()} ${attr.getName()}<sep>,</#list>
</#assign>
${cd4c.constructor("public ${classname}(${pList})")}

super("${packageName}.${classname}");
<#list attrList as attr>
  this.${attr.getName()} = ${attr.getName()};
</#list>

