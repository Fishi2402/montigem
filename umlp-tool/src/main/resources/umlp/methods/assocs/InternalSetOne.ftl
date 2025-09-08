<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "otherSideName", "targetName", "sourceName", "managerName")}

${cd4c.method("public void _internal_set${name?cap_first}(${targetName} o, NotificationScope scope)")}

long oldValue = this._${name}.isPresent()?this._${name}.getGemId():-1;
if(oldValue == o.getGemId()){
  return;
}

this._${name} = new Gem<${targetName}>(o);

<#if otherSideName?has_content>
  if (scope.contains(NotificationScope.LOCAL)) {
    o.${otherSideName}((${sourceName}) this, NotificationScope.MINIMAL);
    if(o.getGemId() != oldValue && ${managerName}.isLoaded${targetName}(oldValue)){
      <#if otherSideName?starts_with("set")>
        ${managerName}.get${targetName}Sure(oldValue).${otherSideName}Gem(new Gem<>(), NotificationScope.MINIMAL);
      <#else>
        <#assign removeOtherSide=otherSideName?replace("add", "remove")>
        ${managerName}.get${targetName}Sure(oldValue).${removeOtherSide}((${sourceName}) this, NotificationScope.MINIMAL);
      </#if>
    }
  }
</#if>

for(${sourceName}Observer obs : ${sourceName?uncap_first}Observers) {
  obs.maybeNotifySet${name?cap_first}((${sourceName}) this, oldValue, o.getGemId(),  scope);
}


