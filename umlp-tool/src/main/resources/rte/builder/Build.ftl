<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "assocList", "isSingleton")}
${cd4c.method("public Optional<${name}> build()")}

if (!buildFinished) {
  if (!isBuildable()) {
    return Optional.empty();
  }

  <#list assocList as assoc>
      <#if genService.isList(assoc)>
        this._${assoc.getName()}.forEach(
          entry -> entry.build()
        );
      </#if>
  </#list>

}
buildFinished = true;
}

return Optional.of(this);
