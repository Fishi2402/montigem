${tc.signature("clazz", "attr", "domain")}
# ${clazz}_add${attr?cap_first}
${clazz?uncap_first} = await domain_manager.get(${domain}.${clazz}, message["gemId"])
await ${clazz?uncap_first}.${attr}s_add(message["indexInList"], message["${attr}GemId"], montigem_runtime.gembase.NotificationScope.LOCAL)
