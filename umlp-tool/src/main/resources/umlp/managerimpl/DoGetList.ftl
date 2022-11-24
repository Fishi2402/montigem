<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
List<Long> ids = DatabaseAccessManager.getAll(${name}.class).stream()
    .map(GemIdentifiable::getGemId)
    .collect(java.util.stream.Collectors.toList());
GemListBuilder<${name}> list = new ${name?cap_first}GemList(ids);
return list.build().get();