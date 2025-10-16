${tc.signature("domain", "clazz", "attr")}

await montigem_runtime.commandmanager.CommandManager.instance.send(
montigem_runtime.commandmanager.Command(commandType="${domain?lower_case}.commands.${clazz?cap_first}_remove${attr?cap_first}",
gemId=${clazz}.gemId,
indexInList=index_in_list,
${attr}=${attr}))
