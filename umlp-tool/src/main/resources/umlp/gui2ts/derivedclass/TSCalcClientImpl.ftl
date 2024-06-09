<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "typeParameter", "domainclass", "package", "isList")}

${cd4c.method("public Promise<${typeParameter}> calc${name?cap_first}()")}
<#if isList>
  return CommandManager.executeCommand(new ${domainclass}_${name?cap_first}_get()).then(res =>
    res.asListResult().get()
  );
<#else>
  return CommandManager.executeCommand(new ${domainclass}_${name?cap_first}_get()).then(res =>
    res.asSimpleResult().get()
  );
</#if>