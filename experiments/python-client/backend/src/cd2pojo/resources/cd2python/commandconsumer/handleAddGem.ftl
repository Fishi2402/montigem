${tc.signature("clazz", "attr", "domain", "type")}
# ${clazz}_add${attr?cap_first}
${clazz?uncap_first} = await domain_manager.get(${domain}.${clazz}.${clazz}, message["gemId"])
await ${clazz?uncap_first}.${attr}_add(message["indexInList"], message["${attr}GemId"], montigem_runtime.gembase.NotificationScope.LOCAL)
