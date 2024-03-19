<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oclRuleName", "domainName", "code")}
${cd4c.method("public static ${domainName}ValidationResult validate${oclRuleName}(${domainName} ${domainName?uncap_first?substring(0,1)})")}
${domainName}ValidationResult ${domainName?uncap_first?substring(0,1)}ValidationResult = new ${domainName}ValidationResult(${domainName?uncap_first?substring(0,1)}.getGemId());
${code}
return ${domainName?uncap_first?substring(0,1)}ValidationResult;
