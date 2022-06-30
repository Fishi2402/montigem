<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected Gem<${name}> _doCreate${name}Gem(long gemId)")}

return new Gem<>(gemId, () -> {
  Optional<${name}> optElem = _do${name}Opt(gemId);
  if(optElem.isPresent()){
    return new LoadResult<>(optElem.get());
  } else {
    return new LoadResult<>(LoadingState.EMPTY);
  }
  });