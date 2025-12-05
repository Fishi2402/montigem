${tc.signature("domainManager", "type", "attr", "isList")}

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->
if ${domainManager}.INSTANCE.is_loaded_locally(${type}, _new_value):
<@indent>
  _new_obj = await ${domainManager}.INSTANCE.get(${type}, _new_value)
  <#if isList>
    await _new_obj.${attr}_add(len(_new_obj.${attr}), _update_obj.gemId, montigem_runtime.gembase.NotificationScope.MINIMAL)
  <#else>
    await _new_obj.set${attr?cap_first}(_update_obj.gemId, montigem_runtime.gembase.NotificationScope.MINIMAL)
  </#if>
</@indent>
if ${domainManager}.INSTANCE.is_loaded_locally(${type}, _old_value):
<@indent>
  # Clear old other side
  _old_obj = await ${domainManager}.INSTANCE.get(${type}, _old_value)
  <#if isList>
    self_index = _old_obj.${attr}.get_index_of_gem_id(_update_obj.gemId)
    await _old_obj.${attr}_rem(self_index, montigem_runtime.gembase.NotificationScope.MINIMAL)
  <#else>
    await _old_obj.set${attr?cap_first}(-1, montigem_runtime.gembase.NotificationScope.MINIMAL)
  </#if>

</@indent>
