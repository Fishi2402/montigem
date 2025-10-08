${tc.signature("domain", "clazz", "attr")}

await montigem_runtime.commandmanager.CommandManager.instance.send(
montigem_runtime.commandmanager.Command(commandType="${domain?lower_case}.commands.${clazz?cap_first}_set${attr?cap_first}",
gemId=${clazz}.gemId,
${attr}GemId=${attr}))
