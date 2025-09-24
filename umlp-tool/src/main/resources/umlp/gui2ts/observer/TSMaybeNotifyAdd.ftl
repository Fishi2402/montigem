<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","initializationTypeName", "type")}

${cd4c.method("void maybeNotifyAdd${name?cap_first}(${initializationTypeName} ${initializationTypeName?uncap_first}, ${type} arg, int indexInList, NotificationScope scope)")}

if(scope.contains(this.getScope())) {
    this.notifyAdd${name?cap_first}(${initializationTypeName?uncap_first}, arg, indexInList);
}
