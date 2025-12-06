${tc.signature("clazz", "attr", "domain", "type")}
# ${clazz}_set{attr?cap_first}
<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->

if domain_manager.is_loaded_locally(${domain}.${clazz}.${clazz}, message["gemId"]):
<@indent>
  ${clazz?uncap_first} = await domain_manager.get(${domain}.${clazz}.${clazz}, message["gemId"])
  await ${clazz?uncap_first}.set${attr?cap_first}(message["${attr}"], montigem_runtime.gembase.NotificationScope.LOCAL)
</@indent>
else:
<@indent>
  import sys
  print(f"Got update for unknown ${clazz} {message["gemId"]}", file=sys.stderr)
</@indent>
