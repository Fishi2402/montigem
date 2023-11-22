<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "name", "type")}
${cd4c.method("public GemList<${type}> _doCreate${name?cap_first}Builder(long ownerGemID, List<Long> initialIds)")}
return new ${name?cap_first}GemList(ownerGemID);