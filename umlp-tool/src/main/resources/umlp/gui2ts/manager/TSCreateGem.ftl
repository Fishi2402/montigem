<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Gem<${name}> create${name}Gem(long gemId)")}

  return this.getSingleton()._doCreate${name}Gem(gemId);
<#else>
  ${cd4c.method("protected Gem<${name}> _doCreate${name}Gem(long gemId)")}

  return new Gem<${name}>().Gem3(gemId, () => {
    let optElem: ${name} = this._do${name}Opt(gemId);
    if (optElem != null) {
      return new LoadResult<${name}>().LoadResult1(optElem);
    } else {
      return new LoadResult<${name}>().LoadResult2(LoadingState.EMPTY);
    }
  });
</#if>