${tc.signature("name", "args")}

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->

if scope.contains(self.getScope()):
<@indent>
  await self.${name}(${args?join(", ")})
</@indent>
