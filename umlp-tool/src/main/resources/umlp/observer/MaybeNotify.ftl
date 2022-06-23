<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className","typeParameter")}
${cd4c.method("void maybeNotifySet${name?cap_first}(${className} ${className?uncap_first},${typeParameter} ${name?uncap_first}, NotificationScope scope)")}

if(scope.contains(getScope())) {
    notifySet${name?cap_first}(${className?uncap_first}, ${name?uncap_first});
}