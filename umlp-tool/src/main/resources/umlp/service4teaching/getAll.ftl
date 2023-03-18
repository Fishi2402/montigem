<#-- (c) https://github.com/MontiCore/monticore -->
${signature("cdName", "cdClasses")}
${cd4c.method("public ResponseEntity<String> getAll(String type)")}
  String msg = "";
  switch (type) {
  <#list cdClasses as c>
    case "${c.getName()}":
      msg = ${cdName}Manager.get${c.getName()}List().stream().map(obj -> new ${c.getName()}DeSer().serialize(obj))
        .collect(Collectors.joining(", "));
      break;
  </#list>
  }
  return ResponseEntity.ok(String.format("[%s]", msg));