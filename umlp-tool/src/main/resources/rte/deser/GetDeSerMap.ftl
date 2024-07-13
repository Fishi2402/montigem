<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classes")}
${cd4c.method("public Map<String, JsonDeSer> getDeSerMap()")}

Map<String, JsonDeSer> map = new HashMap<>();
<#list classes as clazz>
  map.put("${clazz}", new ${clazz}DeSer());
</#list>
return this.map;
