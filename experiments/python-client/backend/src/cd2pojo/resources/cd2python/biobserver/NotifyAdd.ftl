${tc.signature("domainManager", "type", "attr", "isList")}

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->
if ${domainManager}.INSTANCE.is_loaded_locally(${type}, new_gem_id):
<@indent>
  _new_obj = await ${domainManager}.INSTANCE.get(${type}, new_gem_id)
  <#if isList>
    await _new_obj.${attr}_add(len(_new_obj.${attr}), _update_obj.gemId, montigem_runtime.gembase.NotificationScope.MINIMAL)
  <#else>
    await _new_obj.set${attr?cap_first}(_update_obj.gemId, montigem_runtime.gembase.NotificationScope.MINIMAL)
  </#if>
</@indent>
