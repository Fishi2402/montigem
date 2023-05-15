<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}

${cd4c.method("protected GemList<${type}> _doCreate${name?cap_first}(long ownerGemID, int size, List<Long> initialIDs)")}
let list: GemList<${type}> = this.create${name?cap_first}Builder(ownerGemID, []);
list._internal_add(0, initialIDs);
return list.build();