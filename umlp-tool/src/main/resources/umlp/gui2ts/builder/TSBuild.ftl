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
                // TODO: case not handled, replace with jsweet
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
  <#else>
      // TODO: case not handled, replace with jsweet
  </#if>
</#list>
    this.${name?uncap_first}Observers.forEach(obs => obs.notifyBuild(this)); //+ an initial notification
    this.buildFinished = true;
    return this;
  }
}
return null;
