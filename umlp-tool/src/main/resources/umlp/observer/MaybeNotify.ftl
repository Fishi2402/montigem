<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className","typeParameter", "isList")}

<#if isList>
  ${cd4c.method("void maybeNotifySet${name?cap_first}(${className} ${className?uncap_first}, int idx, ${typeParameter} o, NotificationScope scope)")}
  if(scope.contains(getScope())) {
  notifySet${name?cap_first}(${className?uncap_first}, idx, o);
  }
<#else>
  ${cd4c.method("void maybeNotifySet${name?cap_first}(${className} ${className?uncap_first},${typeParameter} o, NotificationScope scope)")}
  if(scope.contains(getScope())) {
  notifySet${name?cap_first}(${className?uncap_first}, o);
  }
</#if>