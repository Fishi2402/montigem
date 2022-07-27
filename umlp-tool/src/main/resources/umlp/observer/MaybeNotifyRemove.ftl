<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className")}

${cd4c.method("void maybeNotifyRemove${name?cap_first}(${className} ${className?uncap_first}, long id, NotificationScope scope)")}

if(scope.contains(getScope())) {
    notifyRemove${name?cap_first}(${className?uncap_first}, id);
}