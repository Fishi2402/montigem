<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className", "type")}

${cd4c.method("void maybeNotifyRemove${name?cap_first}(${className} ${className?uncap_first}, ${type} arg, int idx, NotificationScope scope)")}

if(scope.contains(this.getScope())) {
    this.notifyRemove${name?cap_first}(${className?uncap_first}, arg, idx);
}
