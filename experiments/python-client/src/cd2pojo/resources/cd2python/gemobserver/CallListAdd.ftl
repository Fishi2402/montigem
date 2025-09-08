${tc.signature("name", "obj", "param")}
<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->

for o in self._observer:
<@indent>
  await o.maybeNotify${name}(${obj}=self, ${param}=${param}, index_in_list=index_in_list, scope=scope)
</@indent>
