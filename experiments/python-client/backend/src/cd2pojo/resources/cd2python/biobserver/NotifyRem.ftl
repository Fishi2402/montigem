${tc.signature("domainManager", "type", "attr", "isList")}

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->
if ${domainManager}.INSTANCE.is_loaded_locally(${type}, removed_gem_id):
<@indent>
  _old_obj = await ${domainManager}.INSTANCE.get(${type}, removed_gem_id)
  <#if isList>
    self_index = _old_obj.${attr}.get_index_of_gem_id(_update_obj.gemId)
    actually_removed_gem_id = await _old_obj.${attr}_rem(self_index, montigem_runtime.gembase.NotificationScope.MINIMAL)
    if actually_removed_gem_id != removed_gem_id:
    <@indent>
      raise Exception(f"Removed the wrong gemId {actually_removed_gem_id} instead of {removed_gem_id} from bi-assoc")
    </@indent>
  <#else>
    await _old_obj.set${attr?cap_first}(-1, montigem_runtime.gembase.NotificationScope.MINIMAL)
  </#if>
</@indent>
