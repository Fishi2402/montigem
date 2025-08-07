${tc.signature("clazz", "attr", "domain")}
# ${clazz}_set{attr?cap_first}
${clazz?uncap_first} = await domain_manager.get(${domain}.${clazz}, message["gemId"])
await ${clazz?uncap_first}.set${attr?cap_first}(message["${attr}"], montigem_runtime.gembase.NotificationScope.LOCAL)

# todo: ${attr}GemId for gems!
