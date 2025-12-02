${tc.signature("clazzL", "attrL", "clazzR", "attrR", "domain" )}
<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->


<#macro exchange clazzL attrL clazzR attrR>
  <@indent>
  if domain_manager.is_loaded_locally(${domain}.${clazzR}.${clazzR}, message["${attrL}GemId"]):
  <@indent>
    ${clazzR?uncap_first} = await domain_manager.get(${domain}.${clazzR}.${clazzR}, message["${attrL}GemId"])
    await ${clazzR?uncap_first}.set${attrR?cap_first}(message["${attrR}GemId"], montigem_runtime.gembase.NotificationScope.LOCAL)
  </@indent>
  elif domain_manager.is_loaded_locally(${domain}.${clazzR}.${clazzR}, message["old${attrR?cap_first}GemId"]):
  <@indent>
    # // Does not change other side
    # // Do nothing?
    pass
  </@indent>
  elif domain_manager.is_loaded_locally(${domain}.${clazzL}.${clazzL}, message["${attrR}GemId"]):
  <@indent>
    ${clazzL?uncap_first} = await domain_manager.get(${domain}.${clazzL}.${clazzL}, message["${attrR}GemId"])
    await ${clazzL?uncap_first}.set${attrL?cap_first}(message["${attrL}GemId"], montigem_runtime.gembase.NotificationScope.MINIMAL)
  </@indent>
  else:
  <@indent>
    raise Exception("BIDirectional Set failed?")
  </@indent>
</@indent>

</#macro>

if message["oldOptGemId"] != message["optGemId"]: # exchange Opt
<@exchange clazzR attrR clazzL attrL >
</@exchange>

if message["old${attrR?cap_first}GemId"] != message["${attrR}GemId"]: # exchange ${attrR}
  <@exchange clazzL attrL clazzR attrR>
</@exchange>
