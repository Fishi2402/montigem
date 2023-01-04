<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}

${cd4c.method("protected GemList<${type}> _doCreate${name?cap_first}(long ownerGemID, int size, List<Long> initialIDs)")}
GemList<${type}> list = create${name?cap_first}Builder(ownerGemID, Collections.emptyList());
list._internal_add(0, initialIDs);
return list.build().get();