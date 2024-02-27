<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "attrList")}
<#assign pList>
  <#list attrList as attr>${attr.getName()}: ${attr.printType()}<sep>,</#list>
</#assign>
${cd4c.constructor("public constructor()")}
  public constructor(${pList}) {
    super("${packageName}.${classname}");
<#list attrList as attr>
    this.${attr.getName()} = ${attr.getName()};
</#list>
  }
