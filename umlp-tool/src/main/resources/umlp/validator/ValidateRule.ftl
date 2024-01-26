 <#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oclRuleName", "oclRule", "oclMessage", "domainName", "branchRootNodes")}
${cd4c.method("public static ${domainName}ValidationResult validate${oclRuleName}(${domainName} ${domainName?uncap_first?substring(0,1)})")}
${domainName}ValidationResult ${domainName?uncap_first?substring(0,1)}ValidationResult = new ${domainName}ValidationResult();
String message = "${oclMessage}";
${oclRule}
<#list branchRootNodes as treeNode>
  <#list treeNode.getFinishingCalls(true, "value") as call>
    ${call}
  </#list>
</#list>
return ${domainName?uncap_first?substring(0,1)}ValidationResult;
