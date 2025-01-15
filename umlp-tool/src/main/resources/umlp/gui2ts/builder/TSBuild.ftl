<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "managerName", "assocList", "isSingleton")}
${cd4c.method("public ${name} build()")}

if (!this.buildFinished) {
  if (this.isBuildable()) {

<#list assocList as assoc>
    <#if genService.isList(assoc)>
      this._${assoc.getName()}.build();
    <#else>
      //at the time of writing we assume that every non-list association is a Gem on something which implements GemBuildable
      if (this._${assoc.getName()}.getLoadingState() == LoadingState.LOADED && this._${assoc.getName()}.get().build() == null) {
        //let the build fail
        ${name}ErrorCollector.report${assoc.getName()?cap_first}Invalid(this);
        return null;
      }
    </#if>
</#list>

    if(!this.INTERNAL_deser_mode) {
<#if !isSingleton>
      ${managerName}.add${name}(this).then(() => {
        <#list assocList as assoc>
            <#if genService.isList(assoc)>
                <#if genService.isBidirectional(assoc)>
                    <#assign otherSide=assoc.getSymbol().getOtherSide().getAssocSide()>
                    <#assign thisType=assoc.getSymbol().getType().print()>
                  for (const id of this._${assoc.getName()}) {
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
                if (this._${assoc.getName()}.getLoadingState() == LoadingState.LOADED) {
                <#if genService.isList(otherSide)>
                  if (!this._${assoc.getName()}.get().get${otherSide.getName()?cap_first}().includes(this)) {
                    this._${assoc.getName()}.get().add${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
                  }
                <#else>
                  this._${assoc.getName()}.get().set${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
                </#if>
                }
              </#if>
            </#if>
        </#list>
        this.${name?uncap_first}Observers.forEach(obs => obs.notifyBuild(this)); //+ an initial notification
        this.buildFinished = true;
        return this;
      })
</#if>
    }
<#list assocList as assoc>
  <#if genService.isList(assoc)>
    <#if genService.isBidirectional(assoc)>
      <#assign otherSide=assoc.getSymbol().getOtherSide().getAssocSide()>
      <#assign thisType=assoc.getSymbol().getType().print()>
      for (const id of this._${assoc.getName()}) {
        <#if genService.isList(otherSide)>
          id.add${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
        <#else>
          id.set${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL);
        </#if>
      }
    </#if>
<#--  <#else> -->
<#--    <#if genService.isBidirectional(assoc)> -->
<#--      <#assign otherSide=assoc.getSymbol().getOtherSide().getAssocSide()> -->
<#--      if (this.${name} != null) { -->
<#--      <#if genService.isList(otherSide)> -->
<#--        if (!this._${assoc.getName()}.get().get${otherSide.getName()?cap_first}().includes(this)) { -->
<#--          this._${assoc.getName()}.get().add${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL); -->
<#--        } -->
<#--      <#else> -->
<#--        this._${assoc.getName()}.get().set${otherSide.getName()?cap_first}(this, NotificationScope.MINIMAL); -->
<#--      </#if> -->
<#--      } -->
<#--    </#if> -->
  </#if>
</#list>
    this.${name?uncap_first}Observers.forEach(obs => obs.notifyBuild(this)); //+ an initial notification
    this.buildFinished = true;
    return this;
  }
}
return null;
