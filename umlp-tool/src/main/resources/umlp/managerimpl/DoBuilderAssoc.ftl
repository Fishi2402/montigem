<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "name", "type")}
${cd4c.method("protected  GemListBuilder<${type}> _doCreate${name?cap_first}Builder(long ownerGemID)")}
return new ${domainName}_${name?cap_first}GemList(ownerGemID);