${tc.signature("clazzL", "attrL", "clazzR", "attrR", "domain", "listL", "listR" )}

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->

loadedLeft = domain_manager.is_loaded_locally(${domain}.${clazzL}.${clazzL}, message["${attrR}GemId"])
loadedRight = domain_manager.is_loaded_locally(${domain}.${clazzR}.${clazzR}, message["${attrL}GemId"])


if loadedLeft:
<@indent>
  target = await domain_manager.get(${domain}.${clazzL}.${clazzL}, message["${attrR}GemId"])
   <#if listL>
  index = message["${attrL}Index"] if message["${attrL}Index"] != -1 else target.${attrL}.get_index_of_gem_id(message["${attrL}GemId"])
  old_gem_id = await target.${attrL}_rem(index, montigem_runtime.gembase.NotificationScope.MINIMAL)
  if old_gem_id != message["${attrL}GemId"]:
  <@indent>
    raise Exception(f"Expected to remove left {message["${attrL}GemId"]}, but removed {old_gem_id} at index {index}/{message["${attrL}Index"]} in {target.${attrL}.indexes}")
  </@indent>
  <#else>
    await target.set${attrL?cap_first}(-1, montigem_runtime.gembase.NotificationScope.MINIMAL)
   </#if>
</@indent>
if loadedRight:
<@indent>
  source = await domain_manager.get(${domain}.${clazzR}.${clazzR}, message["${attrL}GemId"])
  <#if listR>
  index = message["${attrR}Index"] if message["${attrR}Index"] != -1 else source.${attrR}.get_index_of_gem_id(message["${attrR}GemId"])
  old_gem_id = await source.${attrR}_rem(index,  montigem_runtime.gembase.NotificationScope.MINIMAL)
  if old_gem_id != message["${attrR}GemId"]:
  <@indent>
    raise Exception(f"Expected to remove right {message["${attrR}GemId"]}, but removed {old_gem_id} at index {index}/{message["${attrR}Index"]} {source.${attrR}.indexes}")
  </@indent>
    <#else>
    await source.set${attrR?cap_first}(-1, montigem_runtime.gembase.NotificationScope.MINIMAL)
   </#if>
</@indent>
if not loadedLeft and not loadedRight:
<@indent>
  raise Exception("Why did the client receive this command, if it does not contain a or p?")
</@indent>
