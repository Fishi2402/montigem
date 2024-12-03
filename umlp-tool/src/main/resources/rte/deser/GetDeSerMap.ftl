<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classes")}
${cd4c.method("public Map<String, JsonDeSer<?>> getDeSerMap()")}

<#list classes as clazz>
  this.deSers.put("${clazz}", new ${clazz}DeSer());
</#list>
return this.deSers;
