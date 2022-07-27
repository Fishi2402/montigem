<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className")}

${cd4c.method("void maybeNotifyAdd${name?cap_first}(${className} ${className?uncap_first}, long id, int indexInList, NotificationScope scope)")}

if(scope.contains(getScope())) {
    notifyAdd${name?cap_first}(${className?uncap_first}, id, indexInList);
}