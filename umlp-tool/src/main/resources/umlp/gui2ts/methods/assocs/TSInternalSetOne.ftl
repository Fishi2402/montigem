<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "otherSideName", "targetName", "sourceName", "managerName")}

${cd4c.method("public void _internal_set${name?cap_first}2(${targetName} o, NotificationScope scope)")}

const oldValue = this._${name}.isPresent()?this._${name}.getGemId():0;

this._${name} = new Gem<${targetName}>().Gem2(o);

<#if otherSideName?has_content>
  if (scope.contains(NotificationScope.LOCAL)) {
  o.${otherSideName}(<${sourceName}> <any> this, NotificationScope.MINIMAL);
  if(${managerName}.isLoaded${targetName}(oldValue)){
    <#if otherSideName?starts_with("set")>
        ${managerName}.get${targetName}Sure(oldValue).${otherSideName}Gem(new Gem<>(), NotificationScope.MINIMAL);
    <#else>
        <#assign removeOtherSide=otherSideName?replace("add", "remove")>
        ${managerName}.get${targetName}Sure(oldValue).then(x => x.${removeOtherSide}(<${sourceName}> <any> this, NotificationScope.MINIMAL));
    </#if>
  }
  }
</#if>

this.${sourceName?uncap_first}Observers.forEach(obs => obs.maybeNotifySet${name?cap_first}(<${sourceName}> <any> this, oldValue, o.getGemId(),  scope));

