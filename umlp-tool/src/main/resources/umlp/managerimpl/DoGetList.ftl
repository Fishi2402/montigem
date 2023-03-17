<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
List<Long> ids = new ArrayList<>(this.known${name}s.keySet());
GemListBuilder<${name}> list = new ${name?cap_first}GemList(ids);
return list.build().get();