<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "isSingleton")}
${cd4c.method("public Promise<GemList<${name}>> _doGet${name}List()")}
if (!this.loaded${name}List) {
  this.loaded${name}List = CommandManager.executeCommand(new ${name}_get_filtered()).then(gemPromiseRes => {
    let res: ListResult<number, Array<number>> = gemPromiseRes.asListResult();
    let list: GemListBuilder<${name}> = new ${name?cap_first}GemList(res.get());
    return list.build();
  });
}
return this.loaded${name}List;
