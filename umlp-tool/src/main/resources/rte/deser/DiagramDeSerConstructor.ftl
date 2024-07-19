<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "deserMapAttr", "classes")}
${cd4c.constructor("public ${cdName?cap_first}()")}

<#list classes as clazz>
  this.${deserMapAttr}.put("${clazz}", new ${clazz}DeSer());
</#list>
