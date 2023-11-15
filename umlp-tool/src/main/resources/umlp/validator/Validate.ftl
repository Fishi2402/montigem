<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "oclRules")}
${cd4c.method("public List<ValidationMessage> validate(${classname} ${classname?uncap_first?substring(0,1)})")}
List<ValidationMessage> validationMessages = new ArrayList<>();
<#list oclRules as rule>
validationMessages.add(${rule}(${classname?uncap_first?substring(0,1)}));
</#list>
return validationMessages;