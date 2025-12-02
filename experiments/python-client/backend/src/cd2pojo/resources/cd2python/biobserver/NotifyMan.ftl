${tc.signature("domainManager", "type", "attr")}

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->
if ${domainManager}.INSTANCE.is_loaded_locally(${type}, _new_value):
<@indent>
  _new_obj = await ${domainManager}.INSTANCE.get(${type}, _new_value)
  await _new_obj.set${attr?cap_first}(_update_obj.gemId, montigem_runtime.gembase.NotificationScope.MINIMAL)
</@indent>
if ${domainManager}.INSTANCE.is_loaded_locally(${type}, _old_value):
<@indent>
  # Clear old other side
  _old_obj = await ${domainManager}.INSTANCE.get(${type}, _old_value)
  await _old_obj.set${attr?cap_first}(-1, montigem_runtime.gembase.NotificationScope.MINIMAL)
</@indent>
