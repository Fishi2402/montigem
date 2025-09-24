<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","initializationTypeName", "type")}

${cd4c.method("void maybeNotifyRemove${name?cap_first}(${initializationTypeName} ${initializationTypeName?uncap_first}, ${type} arg, int idx, NotificationScope scope)")}

if(scope.contains(this.getScope())) {
    this.notifyRemove${name?cap_first}(${initializationTypeName?uncap_first}, arg, idx);
}
