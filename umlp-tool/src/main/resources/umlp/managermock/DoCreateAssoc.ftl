<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "targetName")}

${cd4c.method("protected GemList<${targetName}> _doCreate${assocName}(long gemID, int size, List<Long> initialIds)")}
${assocName}GemList list = new ${assocName}GemList(gemID);
list._internal_add(0, initialIds);
return list.build().get();
