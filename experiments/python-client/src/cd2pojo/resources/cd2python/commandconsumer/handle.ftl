${tc.signature("commands")}

<#list commands as cmd>
  <#if cmd?is_first>if<#else>elif</#if> message["commandType"] == "${cmd.command}":
    [[[indent]]]
    ${tc.includeArgs(cmd.template, ast, [cmd.clazz, cmd.attr, cmd.domain])}
    [[[deindent]]]
</#list>
else:
[[[indent]]]
print("Unhandled command: ", message)
[[[deindent]]]
