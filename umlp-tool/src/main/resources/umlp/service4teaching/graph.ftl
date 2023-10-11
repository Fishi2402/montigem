${signature("cdName", "cdClasses", "typeToAssocSides")}
${cd4c.method("public ResponseEntity<String> graph()")}
List<String> objs = new ArrayList<>();
List<String> links = new ArrayList<>();
<#list cdClasses as c>
${cdName}Manager.get${c.getName()}List()
      .stream()
      .filter(obj -> obj.getClass() == ${c.name}.class || obj.getClass() == ${c.name}Builder.class)
      .map(obj -> "{\"type\": \"${c.getName()}\"," + "\"id\": " + obj.getGemId() + "}")
      .forEach(objs::add);
</#list>

<#list typeToAssocSides?keys as k>
  for(var obj: ${cdName}Manager.get${k}List()){
      <#list typeToAssocSides[k] as entry>
        <#assign side=entry.getValue()>
        <#if genService.isList(side)>
        <#-- List -->
        for(var other : obj.get${side.name?cap_first}()){
          if(obj.getGemId() >= 0 && other.getGemId() >= 0){
            links.add("{\"source\":" + obj.getGemId() + ",\"target\":"+other.getGemId() + "}");
          }
        }
        <#else>
        <#-- Req or Opt-->
        {
          var other = obj.get${side.name?cap_first}Gem();
          if(obj.getGemId() >= 0 && other.getGemId() >= 0){
            links.add("{\"source\":" + obj.getGemId() + ",\"target\":"+other.getGemId() + "}");
          }
        }
        </#if>
      </#list>
}
</#list>

return ResponseEntity.ok(String.format(
      "{\"objects\": [%s], \"links\": [%s]}",
      String.join(",", objs),
      String.join(",", links)
      ));
