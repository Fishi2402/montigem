<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "targetName")}

${cd4c.method("public GemList<${targetName}> _doCreate${assocName}(long gemID, int size, List<Long> initialIds)")}
let list: ${assocName}GemList = new ${assocName}GemList(gemID);
list._internal_add(0, initialIds);
return list.build();
