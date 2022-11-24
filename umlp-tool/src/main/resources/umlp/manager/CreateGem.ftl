<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Gem<${name}> create${name}Gem(long gemId)")}

  return getSingleton()._doCreate${name}Gem(gemId);
<#else>
  ${cd4c.method("protected Gem<${name}> _doCreate${name}Gem(long gemId)")}

  return new Gem<>(gemId, () -> {
  Optional<${name}> optElem = _do${name}Opt(gemId);
  if(optElem.isPresent()){
  return new LoadResult<>(optElem.get());
  } else {
  return new LoadResult<>(LoadingState.EMPTY);
  }
  });
</#if>