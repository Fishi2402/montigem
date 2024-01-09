<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","typeParameter", "domainclass", "package", "isList")}
${cd4c.method("public ${typeParameter} get${name?cap_first}()")}

<#if !isList>
  return CommandManager.executeCommand(new ${package}.commands.derivedclass.${domainclass}_${name?cap_first}_get()).asSimpleResult().get();
<#else>
  return CommandManager.executeCommand(new ${package}.commands.derivedclass.${domainclass}_${name?cap_first}_get()).asListResult().get();
</#if>


