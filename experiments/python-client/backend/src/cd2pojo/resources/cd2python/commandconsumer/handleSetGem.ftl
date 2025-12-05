${tc.signature("clazz", "attr", "domain", "type")}
# ${clazz}_set${attr?cap_first}
${clazz?uncap_first} = await domain_manager.get(${domain}.${clazz}.${clazz}, message["gemId"])
await ${clazz?uncap_first}.set${attr?cap_first}(message["${attr}GemId"], montigem_runtime.gembase.NotificationScope.LOCAL)

