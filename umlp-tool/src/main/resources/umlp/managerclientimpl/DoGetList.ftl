<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "isInterface")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
ListResult<Long, List<Long>> res = CommandManager.executeCommand(new ${name}_get_filtered()).asListResult();
GemListBuilder<${name}> list = new ${name?cap_first}GemList(res.get());
return list.build().get();
