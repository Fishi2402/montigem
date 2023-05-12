<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className", "type")}

${cd4c.method("void maybeNotifyAdd${name?cap_first}(${className} ${className?uncap_first}, ${type} arg, int indexInList, NotificationScope scope)")}

if(scope.contains(this.getScope())) {
    this.notifyAdd${name?cap_first}(${className?uncap_first}, arg, indexInList);
}
