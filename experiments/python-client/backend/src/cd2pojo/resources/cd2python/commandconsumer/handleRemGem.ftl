${tc.signature("clazz", "attr", "domain", "type")}
# ${clazz}_rem${attr?cap_first}
${clazz?uncap_first} = await domain_manager.get(${domain}.${clazz}.${clazz}, message["gemId"])
await ${clazz?uncap_first}.${attr}_rem(message["indexInList"], montigem_runtime.gembase.NotificationScope.LOCAL)
