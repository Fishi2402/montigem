<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "isInterface")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
<#if isInterface>
List<Long> ids = new ArrayList<>();
<#else>
// This does not work if some entries are not loaded from db
List<Long> ids = new ArrayList<>(this.known${name}s.keySet());
</#if>
GemListBuilder<${name}> list = new ${name?cap_first}GemList(ids);
return list.build().get();