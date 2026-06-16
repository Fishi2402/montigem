${tc.signature("clazzL", "attrL", "clazzR", "attrR", "domain", "listL", "listR" )}

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->

loadedLeft = domain_manager.is_loaded_locally(${domain}.${clazzL}.${clazzL}, message["${attrR}GemId"])
loadedRight = domain_manager.is_loaded_locally(${domain}.${clazzR}.${clazzR}, message["${attrL}GemId"])

if loadedLeft:
<@indent>
  target = await domain_manager.get(${domain}.${clazzL}.${clazzL}, message["${attrR}GemId"])
  <#if listL>
    await target.${attrL}_add(len(target.${attrL}) if message["${attrL}Index"] == -1 else message["${attrL}Index"], message["${attrL}GemId"], montigem_runtime.gembase.NotificationScope.MINIMAL)
  <#else>
    await target.set${attrL?cap_first}(message["${attrL}GemId"], montigem_runtime.gembase.NotificationScope.MINIMAL)
  </#if>
</@indent>
if loadedRight:
<@indent>
  source = await domain_manager.get(${domain}.${clazzR}.${clazzR}, message["${attrL}GemId"])
  <#if listR>
    await source.${attrR}_add(len(source.${attrR}) if message["${attrR}Index"] == -1 else message["${attrR}Index"], message["${attrR}GemId"], montigem_runtime.gembase.NotificationScope.MINIMAL)
  <#else >
    await source.set${attrR?cap_first}(message["${attrR}GemId"], montigem_runtime.gembase.NotificationScope.MINIMAL)
  </#if>
</@indent>
if not loadedLeft and not loadedRight:
<@indent>
  raise Exception("Why did the client receive this command, if it does not contain a or p?")
</@indent>
