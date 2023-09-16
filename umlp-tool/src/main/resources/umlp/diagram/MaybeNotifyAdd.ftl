<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("void maybeNotify${name}Added(${name} ${name?uncap_first}, NotificationScope scope)")}
if(scope.contains(this.getScope())){
  this.notify${name}Added(${name?uncap_first});
}
