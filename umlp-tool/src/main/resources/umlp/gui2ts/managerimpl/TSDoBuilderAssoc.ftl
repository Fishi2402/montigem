<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "name", "type")}
${cd4c.method("public Promise<GemList<${type}>> _doCreate${name?cap_first}Builder(long ownerGemID, List<Long> initialIds)")}
return Promise.resolve(new ${domainName}_${name?cap_first}GemList(ownerGemID));