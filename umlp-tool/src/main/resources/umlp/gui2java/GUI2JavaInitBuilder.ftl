<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(args)>

${cd4c.method("protected void init${name?cap_first}(${argsString})")}
this.${name} = ${manager}.${type?uncap_first}Builder();
