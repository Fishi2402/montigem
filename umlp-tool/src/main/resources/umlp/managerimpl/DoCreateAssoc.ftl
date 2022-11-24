<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}

${cd4c.method("protected GemList<${type}> _doCreate${name?cap_first}(long ownerGemID, int size, List<Long> initialIDs)")}
GemListBuilder<${type}> builder = create${name?cap_first}Builder(ownerGemID);
builder._internal_add(0, initialIDs);
return builder.build().get();