<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "isInterface")}
${cd4c.method("public Promise<GemList<${name}>> _doGet${name}List()")}
return CommandManager.executeCommand(new ${name}_get_filtered()).then(gemPromiseRes => {
  let res: ListResult<number, Array<number>> = gemPromiseRes.asListResult();
  let list: GemListBuilder<${name}>  = new ${name?cap_first}GemList(res.get());
  return list.build();
})
