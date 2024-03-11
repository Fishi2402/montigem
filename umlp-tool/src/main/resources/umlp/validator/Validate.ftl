<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "oclRules")}
${cd4c.method("public ${classname}ValidationResult validate(${classname} ${classname?uncap_first?substring(0,1)})")}
${classname}ValidationResult validationResult = new ${classname}ValidationResult();
<#list oclRules as rule>
validationResult.merge(${rule}(${classname?uncap_first?substring(0,1)}));
</#list>
return validationResult;
