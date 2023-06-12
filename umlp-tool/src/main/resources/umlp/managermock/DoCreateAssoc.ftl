<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "targetName")}

${cd4c.method("protected GemList<${targetName}> _doCreate${assocName}(long gemID, int size, List<Long> initialIds)")}
${assocName}GemList list = new ${assocName}GemList(gemID, initialIds);
return list.build().get();
