${tc.signature("commands")}

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->


<#list commands as cmd>
  <#if cmd?is_first>if<#else>elif</#if> message["commandType"] == "${cmd.command}":
    <@indent>
      ${tc.includeArgs(cmd.template, ast, cmd.data)}
    </@indent>
</#list>
else:
<@indent>
  print("Unhandled command: ", message)
</@indent>
