<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName", "assocs")}
${cd4c.method("public Promise<Void> _doAdd${name}2(${name} o, NotificationScope scope)")}

this.${name?uncap_first}s.set(o.getGemId(), o);
this.${name?uncap_first}GemList.push(o);

<#list assocs as assoc>
  <#assign type = assoc.getSymbol().getType().print()>
  <#assign otherSide = assoc.getSymbol().getOtherSide().getAssocSide()>
  <#if genService.isList(assoc)>
  for (const initialId of o._internal_get${assoc.getName()?cap_first}GemList().getIDs(0, o.size${assoc.getName()?cap_first}s())) {
    if(${managerName}Manager.isLoaded${type}(initialId)){
      let other: ${type} = ${managerName}Manager.get${type}Sure(initialId);
      <#if genService.isList(otherSide)>
        other._internal_add${otherSide.getName()?cap_first}(other.size${otherSide.getName()?cap_first}s(), [o.getGemId()], NotificationScope.MINIMAL);
      <#else>
        other._internal_set${otherSide.getName()?cap_first}(o.getGemId(), NotificationScope.MINIMAL);
      </#if>
      }
    }
  <#else>
    if(o.get${assoc.getName()?cap_first}Gem() != null){
    let other: ${type} = o.get${assoc.getName()?cap_first}();
    // let other: ${type} = ${managerName}Manager.get${type}Sure(initialId);
    <#if genService.isList(otherSide)>
      other._internal_add${otherSide.getName()?cap_first}(other.size${otherSide.getName()?cap_first}s(), [o.getGemId()], NotificationScope.MINIMAL);
    <#else>
      other._internal_set${otherSide.getName()?cap_first}(o.getGemId(), NotificationScope.MINIMAL);
    </#if>
    }
  </#if>
</#list>

this.${managerName?uncap_first}Observers.forEach(obs => obs.maybeNotify${name}Added(o, scope));

return Promise.resolve();
