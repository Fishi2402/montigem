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
    //this._${assoc.getName()}.build();
  </#if>
</#list>
if(!INTERNAL_deser_mode) {
${managerName}.add${name}(this);
}
<#list assocList as assoc>
  <#if genService.isList(assoc)>
    <#if genService.isBidirectional(assoc)>
      <#assign otherSide=assoc.getSymbol().getOtherSide().getAssocSide()>
      <#assign thisType=assoc.getSymbol().getType().print()>
      for (${thisType} id: this._${assoc.getName()}) {
          <#if genService.isList(otherSide)>
            id.add${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
          <#else>
            id.set${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
          </#if>
      }
    </#if>
  <#else>
    <#if genService.isBidirectional(assoc)>
      <#assign otherSide=assoc.getSymbol().getOtherSide().getAssocSide()>
      if (is${assoc.getName()?cap_first}Present()) {
      <#if genService.isList(otherSide)>
        if (!this._${assoc.getName()}.get().get${otherSide.getName()?cap_first}().contains(this)) {
          this._${assoc.getName()}.get().add${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
        }
      <#else>
        this._${assoc.getName()}.get().set${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
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
