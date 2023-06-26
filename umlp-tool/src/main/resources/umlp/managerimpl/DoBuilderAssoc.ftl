<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}
${cd4c.method("protected  GemList<${type}> _doCreate${name}Builder(long ownerGemID, List<Long> initialIds)")}
return new ${name}GemList(ownerGemID);
