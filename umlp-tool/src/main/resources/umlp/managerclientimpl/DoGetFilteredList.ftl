<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "isSingleton", "isAbstract")}
${cd4c.method("protected GemList<${name}> _doGetFiltered${name}List(String filter)")}
<#if isSingleton || isAbstract>
  ListResult<Long, List<Long>> res = CommandManager.executeCommand(new ${name}_get_filtered(filter)).asListResult();
  GemListBuilder<${name}> list = new ${name?cap_first}GemList(res.get());
  return list.build().get();
<#else>
  if (this.loaded${name}List.isEmpty()) {
    ListResult<Long, List<Long>> res = CommandManager.executeCommand(new ${name}_get_filtered(filter)).asListResult();
    GemListBuilder<${name}> list = new ${name?cap_first}GemList(res.get());
    this.loaded${name}List = Optional.of(list.build().get());
  }
  return this.loaded${name}List.get();
</#if>
