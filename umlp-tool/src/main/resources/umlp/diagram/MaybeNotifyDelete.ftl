<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("void maybeNotify${name}Deleted(Long gemId, NotificationScope scope) ")}
if(scope.contains(this.getScope())){
  this.notify${name}Deleted(gemId);
}
