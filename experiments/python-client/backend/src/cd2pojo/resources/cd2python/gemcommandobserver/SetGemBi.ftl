${tc.signature("cmdName", "clazz", "thisAttr", "otherAttr")}
await montigem_runtime.commandmanager.CommandManager.instance.send(
montigem_runtime.commandmanager.Command(commandType="${cmdName}",
old${thisAttr?cap_first}GemId=${clazz}.gemId,
${thisAttr}GemId=${clazz}.gemId,
${thisAttr}Index=0,
old${otherAttr?cap_first}GemId=_old_value,
${otherAttr}GemId=${otherAttr},
${otherAttr}Index=0))
