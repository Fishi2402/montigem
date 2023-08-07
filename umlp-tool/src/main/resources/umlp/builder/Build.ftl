<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "managerName", "assocList")}
${cd4c.method("public Optional<${name}> build()")}

if (!buildFinished) {
if (isBuildable()) {
<#list assocList as assoc>
  <#if genService.isList(assoc)>
    this._${assoc.getName()}.build();
  </#if>
</#list>
if(!INTERNAL_deser_mode) {
${managerName}.add${name}(this);
}
<#list assocList as assoc>
  <#if genService.isList(assoc)>
    <#if genService.isBidirectional(assoc)>
      <#assign otherSide=assoc.getSymbol().getOtherSide().getAssocSide()>
      <#assign otherType=assoc.getSymbol().getType().print()>
      for (long id: this._${assoc.getName()}.getIDs()) {
        if (${managerName}.isLoaded${otherType}(id)) {
          <#if genService.isList(otherSide)>
          ${managerName}.get${otherType}Sure(id).add${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
          <#else>
          ${managerName}.get${otherType}Sure(id).set${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
          </#if>
        }
      }
    </#if>
  <#else>
    <#if genService.isBidirectional(assoc)>
      <#assign otherSide=assoc.getSymbol().getOtherSide().getAssocSide()>
      <#assign otherType=assoc.getSymbol().getType().print()>
      if (${managerName}.isLoaded${otherType}(this._${assoc.getName()}.getGemId())) {
      <#if genService.isList(otherSide)>
      ${managerName}.get${otherType}Sure(this._${assoc.getName()}.getGemId()).add${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
      <#else>
      ${managerName}.get${otherType}Sure(this._${assoc.getName()}.getGemId()).set${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
      </#if>
      }
      </#if>
    </#if>
</#list>

${name?uncap_first}Observers.forEach(obs -> obs.notifyBuild(this)); //+ an initial notification
buildFinished = true;

return Optional.of(this);
}
}
return Optional.empty();
