<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "name", "type")}
${cd4c.method("protected  GemList<${type}> _doCreate${name?cap_first}Builder(long ownerGemID, List<Long> initialIds)")}
return new ${domainName}_${name?cap_first}GemList(ownerGemID);