<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "otherSideName", "targetName", "sourceName", "managerName")}

${cd4c.method("public void _internal_set${name?cap_first}(long gemID, NotificationScope scope)")}

long oldValue = this._${name}.isPresent()?this._${name}.getGemId():0;

if(${managerName}.isLoaded${targetName}(gemID)){
  this._${name} = new Gem<${targetName}>(${managerName}.get${targetName}Sure(gemID));
}

<#if otherSideName?has_content>
  if (scope.contains(NotificationScope.LOCAL)) {
    if(${managerName}.isLoaded${targetName}(gemID)){
      ${managerName}.get${targetName}Sure(gemID).${otherSideName}((${sourceName}) this, NotificationScope.MINIMAL);
    }
  }
</#if>

for(${sourceName}Observer obs : ${sourceName?uncap_first}Observers) {
  obs.maybeNotifySet${name?cap_first}((${sourceName}) this, oldValue, gemID,  scope);
}


