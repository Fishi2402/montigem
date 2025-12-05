${tc.signature("cmdName", "clazz", "thisAttr", "otherAttr")}
await montigem_runtime.commandmanager.CommandManager.instance.send(
montigem_runtime.commandmanager.Command(commandType="${cmdName}",
${thisAttr}GemId=${clazz}.gemId,
${thisAttr}Index=-1,
${otherAttr}GemId=${otherAttr},
${otherAttr}Index=index_in_list))
